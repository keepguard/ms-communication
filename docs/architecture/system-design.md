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
  - `POST /api/v1/messages/send` — envio com fallback (header `X-Company-Id`).
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
