package com.keepguard.ms_communication.adapters.in.rest.message;

import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.lib_common.metrics.annotation.MetricsEndpoint;
import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.ms_communication.adapters.in.rest.message.mapper.MessageAdapterMapper;
import com.keepguard.ms_communication.adapters.in.rest.message.dto.response.MessageSendResponseDTO;
import com.keepguard.ms_communication.adapters.in.rest.message.dto.request.MessageSendRequestDTO;
import com.keepguard.ms_communication.application.port.in.service.MessagePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mensagens", description = "APIs para envio de mensagens usando templates configurados")
public class MessageController {

    private final MessagePort messagePort;
    private final MessageAdapterMapper adapterMapper;

    @PostMapping("/send")
    @Operation(
        summary = "Enviar mensagem",
        description = "Envia uma mensagem usando um template configurado. " +
                    "A mensagem será processada de acordo com o tipo especificado (EMAIL, SMS, WHATSAPP, etc.) " +
                    "e enviada através do provedor de comunicação apropriado.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados da mensagem a ser enviada",
            required = true,
            content = @Content(schema = @Schema(example = """
                {
                  "messageType": "EMAIL",
                  "recipient": "usuario@exemplo.com",
                  "codeUser": "123e4567-e89b-12d3-a456-426614174000",
                  "templateType": "WELCOME",
                  "subject": "Bem-vindo ao KeepGuard",
                  "variables": {
                    "userName": "João Silva",
                    "activationLink": "https://keepguard.com/activate"
                  }
                }
                """))
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mensagem enviada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou template não encontrado"),
        @ApiResponse(responseCode = "401", description = "Aplicação não autorizada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor ou falha no envio")
    })
    @MetricsEndpoint(
        endpoint = "message_send",
        operation = "enviar mensagem"
    )
    @LogOperation(
        operation = "SEND_MESSAGE",
        description = "Enviando mensagem - tipo: {dto.messageType}, destinatário: {dto.recipient}, codeUser: {dto.codeUser}, application: {xApplication}",
        audit = true,
        auditAction = "SEND_MESSAGE",
        auditEntityType = "MESSAGE"
    )
    public ResponseEntity<MessageSendResponseDTO> send(
            @Valid @RequestBody MessageSendRequestDTO dto,
            @Parameter(description = "UUID da aplicação", required = true)
            @RequestHeader("X-Application") String xApplication) {

        log.info("Realizando envio de mensagem - tipo: {}, destinatário: {}, codeUser: {}, application: {}", 
            dto.getMessageType(), dto.getRecipient(), dto.getCodeUser(), xApplication);

        var xApplicationUuid = ValidationUtils.validateXApplication(xApplication);

        var command = adapterMapper.toSendCommand(dto, xApplicationUuid);
        boolean sent = messagePort.sendWithFallback(command);
        
        var response = MessageSendResponseDTO.builder()
                .success(sent)
                .message(sent ? "Mensagem enviada com sucesso" : "Falha ao enviar mensagem")
                .build();
        
        log.info("Mensagem {} - tipo: {}, destinatário: {}, codeUser: {}, application: {}", 
            sent ? "enviada com sucesso" : "falhou ao enviar", 
            dto.getMessageType(), dto.getRecipient(), dto.getCodeUser(), xApplication);
        
        return ResponseEntity.ok(response);
    }


}