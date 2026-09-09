package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.consumer;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.adapters.out.feign.BillingUserClient;
import com.keepguard.ms_communication.application.dto.message.MessageSendCommandDTO;
import com.keepguard.ms_communication.application.port.in.MessagePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingInvoiceFactRabbitMQConsumer {

    private final MessagePort messagePort;
    private final BillingUserClient userClient;
    private final StringRedisTemplate redis;

    @Value("${billing.email.idempotency-ttl-hours:168}")
    private long idempotencyTtlHours;

    @RabbitListener(queues = "${rabbitmq.queues.billing-invoice:ms.communication.billing.invoice.local}")
    public void onBillingInvoiceFact(@Payload Map<String, Object> payload) {
        String invoiceId = stringOf(payload.get("invoiceId"));
        String userId = stringOf(payload.get("userId"));
        String companyId = stringOf(payload.get("companyId"));
        if (invoiceId.isBlank() || userId.isBlank() || companyId.isBlank()) {
            log.warn("Fato billing.invoice ignorado: payload incompleto");
            return;
        }
        boolean paid = payload.containsKey("amountCents") && !payload.containsKey("graceEndsAt");
        // overdue has graceEndsAt; paid has amountCents. Both may coexist — prefer overdue key if graceEndsAt present.
        if (payload.containsKey("graceEndsAt") && stringOf(payload.get("graceEndsAt")).length() > 0) {
            paid = false;
        } else if (payload.containsKey("amountCents")) {
            paid = true;
        }
        String kind = paid ? "paid" : "overdue";
        String idempotencyKey = "billing:email:" + kind + ":" + invoiceId;
        Boolean first = redis.opsForValue().setIfAbsent(idempotencyKey, "1", Duration.ofHours(idempotencyTtlHours));
        if (Boolean.FALSE.equals(first)) {
            log.info("E-mail billing já enviado invoiceId={} kind={}", invoiceId, kind);
            return;
        }

        String email;
        try {
            Map<String, Object> user = userClient.getUserById(UUID.fromString(userId), UUID.fromString(companyId));
            email = stringOf(user == null ? null : user.get("email"));
        } catch (RuntimeException ex) {
            log.warn("Não foi possível resolver e-mail do pagador userId={} companyId={}", userId, companyId);
            redis.delete(idempotencyKey);
            return;
        }
        if (email.isBlank()) {
            log.warn("Pagador sem e-mail userId={} — e-mail de fatura não enviado", userId);
            return;
        }

        TemplateTypeEnum template = paid ? TemplateTypeEnum.FATURA_PAGA : TemplateTypeEnum.FATURA_VENCIDA;
        Map<String, Object> variables = new HashMap<>();
        variables.put("invoiceId", invoiceId);
        variables.put("amountCents", payload.get("amountCents"));
        variables.put("graceEndsAt", payload.get("graceEndsAt"));

        String subject = paid ? "Pagamento confirmado" : "Fatura em atraso";
        String content = paid
                ? "<p>Recebemos o pagamento da sua fatura. Acesse Planos no KeepGuard para detalhes.</p>"
                : "<p>Sua fatura está em atraso. Regularize em Planos no KeepGuard.</p>";

        MessageSendCommandDTO command = MessageSendCommandDTO.builder()
                .companyId(UUID.fromString(companyId))
                .communicationType(CommunicationTypeEnum.EMAIL)
                .recipient(email)
                .codeUser(userId)
                .subject(subject)
                .content(content)
                .messageType("EMAIL")
                .templateType(template.name())
                .variables(variables)
                .build();

        try {
            boolean sent = messagePort.sendWithFallback(command);
            if (!sent) {
                log.warn("Falha ao enviar e-mail billing invoiceId={}", invoiceId);
                redis.delete(idempotencyKey);
            }
        } catch (RuntimeException ex) {
            log.warn("Erro ao enviar e-mail billing invoiceId={}", invoiceId);
            redis.delete(idempotencyKey);
            throw ex;
        }
    }

    private static String stringOf(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
