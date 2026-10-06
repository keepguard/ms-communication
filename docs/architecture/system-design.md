# ms-communication - System Design

## 1. Propósito e Domínio
- **Responsabilidade Principal:** Orquestrar o envio de comunicações (e-mail, SMS, WhatsApp, push) via provedores configuráveis, gerenciar templates multi-tenant e reagir a fatos de billing para notificar pagadores. Atua como hub de despacho com fallback entre provedores e processamento assíncrono via RabbitMQ.
- **Domínio/Subdomínio:** Notificações / Comunicação omnichannel (suporte a Billing para faturas pagas/vencidas).

## 2. Tech Stack Local
- **Linguagem & Framework:** Java 25 / Spring Boot 3.5.3 / Spring Cloud 2025.0.3 (OpenFeign) / Spring AMQP / Spring Data JPA / Spring Data Redis / Resilience4j 2.2.0 / Springdoc OpenAPI 2.3.0 / Lombok / Virtual Threads habilitados. Dependência compartilhada: `lib-common` 1.0.37-SNAPSHOT. Versão do artefato: `1.0.34`.
- **Persistência e Cache:** PostgreSQL (`keepguard_api_db`, schema Hibernate `ms_communication`; ddl-auto `update` em local/dev, `validate` em prod). Redis para cache de Template/Provider (TTL 7 dias) e idempotência de e-mails de billing; standalone em local, cluster de 6 nós em dev/prod.
- **Mensageria:** RabbitMQ — consome `message-send` e `billing-invoice`; publica para exchange próprio (`message.sent` / `message.failed`), DLQ, `srv-email-google-sender`, fila `keepguard.notifications.sms` e audit (`srv-audit-exchange-*`).

## 3. Arquitetura Interna
- **Padrão Utilizado:** Hexagonal (Ports & Adapters) + DDD leve + CQRS parcial (Command/Query/UseCase por agregado). Camadas: `domain` → `application` (ports in/out + services) → `adapters` (in: REST/RabbitMQ; out: Feign/RabbitMQ/e-mail/SMS) + `infrastructure` (JPA, Redis, strategies de provedor, resilience, métricas).
- **Módulos Principais:**
  - **Agregados de domínio:** `Provider` (tipo, canal, ativo/default, limites, URL/config) e `Template` (tipo, messageType, `companyId`, subject/content/variables).
  - **Application:** `Message*`, `Provider*`, `Template*` (UseCase/Command/Query); ports de cache, persistence, e-mail dinâmico, event publisher e metrics.
  - **Adapters in:** `MessageController`, `ProviderController`, `TemplateController`, `HealthController`; consumers `MessageSendRabbitMQConsumer`, `BillingInvoiceFactRabbitMQConsumer`.
  - **Adapters out / Infrastructure:** estratégias `N8N`, `SENDGRID`, `EMAIL_GOOGLE_SENDER`, `SRV_SMS_SENDER`; Feign (`BillingUserClient`, `N8nWebhookClient`, `DynamicEmailSenderClient`); producers e-mail/SMS; JPA adapters + Redis cache; `RabbitMQEventPublisherAdapter`; Actuator/Prometheus; filtro de correlation-id.

## 4. Superfície de Contato (I/O)
- **Endpoints Expostos Principais:** Porta `8082` (local `8582`).
  - `POST /api/v1/messages/send` — envio com fallback (header `X-Company-Id`). Contrato completo no §6.
  - `CRUD /api/v1/templates` (+ `GET /by-type`) — multi-tenant via `X-Company-Id`.
  - `CRUD /api/v1/providers` — activate/deactivate/default, listagens por tipo/ativos, enums de tipos.
  - `GET /api/v1/health` + Actuator `health`/`info`/`prometheus`; Swagger UI habilitado.
- **Dependências Externas:**
  - **ms-user (interno):** Feign `GET /internal/v1/users/{id}` com `X-Company-Id` (`USER_SERVICE_URL`, default `http://localhost:8085`) — resolução de e-mail no fluxo de fatura.
  - **ms-billing (eventos):** exchange `ms-billing-exchange-*`, routing keys `billing.invoice.paid` / `billing.invoice.overdue` → fila `ms.communication.billing.invoice.*`.
  - **Provedores de envio:** n8n (webhook Feign dinâmico), SendGrid, `srv-email-google-sender` (RabbitMQ), `srv-sms-sender` (fila `keepguard.notifications.sms`).
  - **Observabilidade/Audit:** Logstash; publicação de auditoria via `lib-common` (`keepguard.audit` → `srv-audit-exchange-*`).

## 5. Invariantes Locais e Observações
- **Multi-tenancy:** Templates e envios escopados por `companyId` (header `X-Company-Id` / MDC `X-Tenant-Id` no consumer Rabbit). Lookup de usuário billing exige o mesmo header.
- **Fallback de provedores:** `sendWithFallback` itera provedores ativos do `CommunicationTypeEnum` até sucesso; `sendWithProvider` exige UUID explícito.
- **Provider default/ativo:** domínio com `activate`/`deactivate`/`setAsDefault`; apenas um default por canal é esperado via APIs de patch.
- **Idempotência billing:** chave Redis `billing:email:{paid|overdue}:{invoiceId}` (TTL default 168h); falha de envio ou de resolução de e-mail remove a chave para retry.
- **Templates:** processamento por `TemplateTypeEnum` + `MessageTypeEnum` + `companyId`; falha no processador não aborta — usa subject/content originais do comando.
- **ACK manual Rabbit:** validação → nack sem requeue (DLQ); erro de processamento → nack com requeue; CircuitBreaker/Retry em consumer e clients n8n.
- **Schema isolado:** persistência exclusiva em `ms_communication`; cache Redis com prefixos `template_cache` / `provider_cache` / `providers_by_type`.
- **Peculiaridade de config:** no `application.yml` base, bloco Rabbit de conexão aparece sob `management.rabbitmq` (perfis local/dev/prod usam corretamente `spring.rabbitmq`). Sem `.env.example` no serviço.

## 6. Contrato de envio de mensagem (para outros serviços)

Conferido no código em 2026-10-06 (`MessageController`, `MessageSendRequestDTO`, `MessageSendRabbitMQConsumer`/`DTO`, `MessageCommandService`, `TemplateProcessorService`, `RabbitMQProducerConfig`, `application-prod.yml`). Os dois caminhos (HTTP e fila) caem no mesmo `sendWithFallback`.

### 6.1 Destinatário
- `recipient` é o **endereço final** (e-mail no canal EMAIL; telefone no SMS). O ms-communication **não resolve usuário** neste fluxo: quem chama já manda o e-mail.
- `codeUser` é só rótulo de log/auditoria (string livre ≤ 100; não é consultado em lugar nenhum).
- A única resolução de e-mail interna (`ms-user GET /internal/v1/users/{id}`) é do fluxo de **fatura** (`billing-invoice`), não deste contrato. Quem precisar do e-mail a partir de um id resolve por conta própria no ms-user (rotas internas exigem JWT `ROLE_SYSTEM`/`ROLE_ADMIN`, obtido via OAuth `client_credentials` no ms-auth).

### 6.2 Corpo (HTTP e fila)
| Campo | Tipo | Obrig. | Observação |
|---|---|---|---|
| `messageType` | enum `EMAIL\|SMS\|PUSH_NOTIFICATION\|WHATSAPP\|PUSH` | sim | escolhe o template (`MessageTypeEnum`) |
| `communicationType` | enum `EMAIL\|SMS\|PUSH_NOTIFICATION\|WHATSAPP\|TELEGRAM\|SENDGRID\|PUSH` | sim | escolhe os provedores ativos (fallback em ordem) |
| `recipient` | string ≤ 200 | sim | e-mail/telefone final |
| `templateType` | enum (`TemplateTypeEnum`) | sim | ex.: `NOTIFICACAO_GERAL`, `ALERTA_SEGURANCA`, `CONFIRMACAO_ACAO`… — obrigatório mesmo no corpo livre |
| `subject` | string ≤ 200 | não | assunto (corpo livre) |
| `content` | string ≤ 1000 | não | corpo; no e-mail vai como **HTML** |
| `variables` | objeto `{nome: valor}` | não | substitui `{{nome}}` no template |
| `codeUser` | string ≤ 100 | não | só log |
| `companyId` | string UUID | **só na fila** | no HTTP vem do header |
| `xCorrelationId` | string ≤ 100 | **só na fila (obrig.)** | alias `correlationId` |

Enums são o **nome** (`EMAIL`), não o valor minúsculo. Campos desconhecidos são ignorados.

**Template × corpo livre** (regra de `sendViaProvider`):
- `variables` **ausente ou vazio** → usa `subject`/`content` do payload como estão (corpo livre; o template não é consultado).
- `variables` com itens → busca template ativo por (`templateType`, `messageType`, `companyId`); achou → usa subject/content do template com `{{var}}` substituídas (variável ausente fica literal); não achou ou erro → volta silenciosamente para `subject`/`content` do payload.

### 6.3 HTTP — `POST /api/v1/messages/send`
- URL no cluster: `http://ms-communication:8082/api/v1/messages/send` (Service `ms-communication`, namespace `keepguard`).
- Headers: `Content-Type: application/json`; `X-Company-Id: <UUID>` **obrigatório**; `X-Correlation-ID` opcional (devolvido na resposta); `X-Tenant-Id` opcional (no provedor `EMAIL_GOOGLE_SENDER` vira o `companyId` repassado ao `srv-email-google-sender`; sem ele, `keepguard-guardian`).
- **Autenticação: nenhuma.** O serviço não tem Spring Security nem filtro de JWT; não há JWT de serviço/`ROLE_SYSTEM`. Os chamadores reais (`ms-auth-go`, `ms-ai-guardian-go`) mandam só `X-Company-Id`. Não há NetworkPolicy no chart: qualquer pod do cluster alcança a porta.
- Respostas:
  - `200 {"success": true, "message": "Mensagem enviada com sucesso"}`;
  - `200 {"success": false, "message": "Falha ao enviar mensagem"}` — **todos os provedores falharam**; o chamador precisa olhar `success`, não só o status;
  - `400` ProblemDetail `validation-error` com `errors{campo: msg}` (bean validation do corpo);
  - `404` ProblemDetail — nenhum provedor ativo para o `communicationType`;
  - `500` ProblemDetail genérico — header `X-Company-Id` ausente/não-UUID, enum inválido ou JSON ilegível (não há handler específico, cai no `Exception`).
- Latência: o envio é síncrono e passa por todos os provedores do canal (clientes HTTP com read timeout de 30 s); chamadores devem usar timeout próprio e não bloquear fluxo crítico.

### 6.4 Fila — `message-send`
- Exchange **topic** durável `ms-communication-exchange-{env}` (prod: `ms-communication-exchange-prod`), routing key `communication.message.send` → fila durável `ms.communication.message.send.{env}` (prod: `ms.communication.message.send.prod`), com DLX `ms-communication-exchange-dlt-{env}`. Exchange e fila são declarados pelo próprio ms-communication.
- Mensagem: JSON do §6.2 com `companyId` (UUID) e `xCorrelationId` no corpo; `content_type=application/json` (conversor Jackson; sem `__TypeId__`, o tipo é inferido pelo listener). Recomenda-se `delivery_mode=2`.
- Processamento: ACK manual, `prefetch 1`. Campo obrigatório ausente → `nack` sem requeue (DLQ). Falha de envio (provedores, template, `companyId` não-UUID) é **logada e a mensagem é confirmada** — não há resposta, retry nem evento de resultado para o publicador.
