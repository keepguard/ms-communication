# ms-communication → Go: comparação com `lib-go-common`

Levantamento do que o `ms-communication` usa hoje de `lib-common`/`lib-security`/
`lib-validation` (Java) contra o que já existe em `lib-go-common`, para preparar a
migração. Não envolve escrita de código.

## 1. Dependências declaradas no pom.xml x uso real

`pom.xml:106-110` declara **apenas** `lib-common` (groupId `com.keepguard`,
artifactId `lib-common`, versão `1.0.37-SNAPSHOT`). **Não há** dependência de
`lib-security` nem `lib-validation` no `pom.xml`, e a busca por imports
(`com.keepguard.lib_security.*`, `com.keepguard.lib_validation.*`) em todo
`src/main/java` não retornou nenhuma ocorrência. O serviço não usa essas duas libs.

De `lib-common`, o uso real (confirmado por import + uso no corpo do código,
não só import morto) é:

| Classe/pacote `lib-common` | Onde é usado (arquivo:linha) | Uso real? |
|---|---|---|
| `communication.enums.CommunicationTypeEnum` | ~25 arquivos em `domain/entity/Provider.java:3`, `application/dto/provider/*`, `adapters/in/rest/provider/**`, `infrastructure/persistence/**`, `infrastructure/provider/**` | Sim — campo de domínio, DTO, path/query param, persistência |
| `communication.enums.MessageTypeEnum` | ~15 arquivos em `domain/entity/Template.java:3`, `application/dto/template/*`, `application/dto/message/MessageSendCommandDTO.java`, `adapters/in/rest/template/**`, `adapters/in/rest/message/**` | Sim |
| `communication.enums.TemplateTypeEnum` | ~15 arquivos, mesmos módulos do MessageType | Sim |
| `logging.annotation.LogOperation` | `application/service/template/TemplateCommandService.java:6`, `TemplateProcessorService.java:7`, `application/service/message/MessageCommandService.java:5`, `MessageRabbitMQProcessorService.java:3`, `application/service/provider/ProviderCommandService.java:6` | Sim — 13 métodos anotados (lista completa no item 3) |
| `metrics.annotation.MetricsEndpoint` | `adapters/in/rest/template/TemplateController.java:3`, `adapters/in/rest/provider/ProviderController.java:3`, `adapters/in/rest/message/MessageController.java:5` | Sim — 20 endpoints anotados (lista completa no item 3) |
| `metrics.service.MetricsService` | `infrastructure/metrics/MetricsAdapter.java:3` | Sim — injetado e delegado 1:1 (`startSample`, `incrementCounter`, `recordTimer`, `setGauge`, `recordSuccess`, `recordError`, `recordMessageSend`) |
| `config.MetricsConfig` | `MsCommunicationApplication.java:3` | Importado (ativa a auto-configuração dos beans de métricas da lib); não há chamada direta no código do serviço, é configuração de boot |
| `utils.ValidationUtils` | importado em `TemplateController.java:4`, `ProviderController.java:4`, `MessageController.java:6` | **Não** — import presente mas nenhuma chamada `ValidationUtils.*` encontrada nesses 3 arquivos (import morto/resquício de refactor) |

Classes de `lib-common` **não usadas** pelo ms-communication (mesmo estando
disponíveis na lib): `audit.*` (o serviço não publica eventos de auditoria via
lib-common — o `@LogOperation` com `audit=true` é tratado pelo aspecto da própria
lib, mas o publisher RabbitMQ de auditoria do ms-communication é customizado, ver
item 5), `utils.BrazilianValidationUtils`, `utils.CodeGeneratorUtils`,
`utils.DateConverter`, `utils.StringSanitizer`, `exception.InvalidPasswordException`,
`exception.InvalidTenantIdException`.

## 2. JWT / lib-security — escopo de auth

**Não usa.** Confirmado por:
- `pom.xml` sem a dependência `lib-security`.
- Nenhum import de `com.keepguard.lib_security.*` em `src/main/java`.
- Nenhuma referência a `Authorization`, `Bearer`, `JWT`, `SecurityConfig` ou
  `@EnableJwtSecurity` em todo o código fonte.
- Todos os controllers (`TemplateController`, `ProviderController`,
  `MessageController`) resolvem o tenant só pelo header `X-Company-Id`
  (ex.: `TemplateController.java:66`, `ProviderController.java:68`,
  `MessageController.java:72`), sem checagem de token.

**Conclusão:** o ms-communication é um serviço interno sem autenticação própria
(presumivelmente atrás de rede/gateway). A fase "auth na lib Go" (pacote `auth`
de `lib-go-common`) **não entra no escopo** desta migração — não há JWT/claims/
tenant-via-token para portar, só o header `X-Company-Id` lido diretamente.

Por completude: `lib-go-common/auth` (lido em `auth/verifier.go` e
`auth/middleware.go`) já implementa `Verifier.Parse` (HS256, `iss=ms-auth`, `exp`
obrigatório), `CheckTenant` e `Middleware` net/http com claims/roles/tenant — ou
seja, se um dia o ms-communication-go precisar de auth, a peça já existe pronta e
não precisa ser escrita. Mas hoje isso é fora do escopo real do serviço.

## 3. `@LogOperation` e `@MetricsEndpoint` — todas as ocorrências

### 3.1 `@LogOperation` (13 ocorrências)

Assinatura real da anotação (`lib-common/.../logging/annotation/LogOperation.java:28-62`):
`operation()`, `description()` (default ""), `contextProvider()` (default ""),
`audit()` (default true), `auditAction()` (default ""), `auditEntityType()`
(default "ENTITY"). Não existem parâmetros `Name`/`Action`/`ResourceType`/`Reason`
— esses nomes mapeiam para `operation`/`auditAction`/`auditEntityType`/`description`
respectivamente (ver `oplog.Operation` no item abaixo).

| # | arquivo:linha | operation | description | audit | auditAction | auditEntityType |
|---|---|---|---|---|---|---|
| 1 | `application/service/template/TemplateCommandService.java:31` | `CREATE_TEMPLATE` | `Criando novo template: {command.name}` | true | `CREATE` | `TEMPLATE` |
| 2 | `application/service/template/TemplateCommandService.java:78` | `UPDATE_TEMPLATE` | `Atualizando template: {id}` | true | `UPDATE` | `TEMPLATE` |
| 3 | `application/service/template/TemplateCommandService.java:119` | `DELETE_TEMPLATE` | `Removendo template: {id}` | true | `DELETE` | `TEMPLATE` |
| 4 | `application/service/template/TemplateCommandService.java:141` | `ACTIVATE_TEMPLATE` | `Ativando template: {id}` | true | `ACTIVATE` | `TEMPLATE` |
| 5 | `application/service/template/TemplateCommandService.java:160` | `DEACTIVATE_TEMPLATE` | `Desativando template: {id}` | true | `DEACTIVATE` | `TEMPLATE` |
| 6 | `application/service/template/TemplateProcessorService.java:28` | `PROCESS_TEMPLATE` | `Processando template: {templateType} com variáveis: {variables}` | true | `PROCESS` | `TEMPLATE` |
| 7 | `application/service/message/MessageCommandService.java:36` | `SEND_MESSAGE_WITH_PROVIDER` | `Enviando mensagem via provedor: {providerId} para: {command.recipient}` | true | `SEND_MESSAGE` | `MESSAGE` |
| 8 | `application/service/message/MessageCommandService.java:78` | `SEND_MESSAGE_WITH_FALLBACK` | `Enviando mensagem com fallback para: {command.recipient}` | true | `SEND_MESSAGE` | `MESSAGE` |
| 9 | `application/service/message/MessageRabbitMQProcessorService.java:21` | `PROCESS_RABBITMQ_MESSAGE_SEND` | `Processando mensagem de envio via RabbitMQ - xCorrelationId: {rabbitMQMessage.xCorrelationId}, recipient: {rabbitMQMessage.recipient}` | **false** | `PROCESS_MESSAGE_SEND` | `MESSAGE` |
| 10 | `application/service/provider/ProviderCommandService.java:31` | `CREATE_PROVIDER` | `Criando novo provedor: {command.name}` | true | `CREATE` | `PROVIDER` |
| 11 | `application/service/provider/ProviderCommandService.java:91` | `UPDATE_PROVIDER` | `Atualizando provedor: {id}` | true | `UPDATE` | `PROVIDER` |
| 12 | `application/service/provider/ProviderCommandService.java:135` | `DELETE_PROVIDER` | `Removendo provedor: {id}` | true | `DELETE` | `PROVIDER` |
| 13 | `application/service/provider/ProviderCommandService.java:157` | `ACTIVATE_PROVIDER` | `Ativando provedor: {id}` | true | `ACTIVATE` | `PROVIDER` |
| 14 | `application/service/provider/ProviderCommandService.java:177` | `DEACTIVATE_PROVIDER` | `Desativando provedor: {id}` | true | `DEACTIVATE` | `PROVIDER` |
| 15 | `application/service/provider/ProviderCommandService.java:197` | `SET_PROVIDER_AS_DEFAULT` | `Definindo provedor como padrão: {id}` | true | `UPDATE` | `PROVIDER` |

(15 ocorrências, não 13 — correção do número estimado inicialmente.)

### 3.2 `@MetricsEndpoint` (20 ocorrências)

Assinatura: `endpoint()`, `applicationParam()` (default "", vira "none"),
`operation()` (default "").

| # | arquivo:linha | endpoint | operation |
|---|---|---|---|
| 1 | `adapters/in/rest/template/TemplateController.java:59` | `template_create` | `criar template` |
| 2 | `adapters/in/rest/template/TemplateController.java:90` | `template_update` | `atualizar template` |
| 3 | `adapters/in/rest/template/TemplateController.java:120` | `template_delete` | `deletar template` |
| 4 | `adapters/in/rest/template/TemplateController.java:147` | `template_get_by_id` | `buscar template por ID` |
| 5 | `adapters/in/rest/template/TemplateController.java:176` | `template_get_by_type_and_application` | `buscar template por tipo` |
| 6 | `adapters/in/rest/template/TemplateController.java:204` | `template_search` | `listar templates` |
| 7 | `adapters/in/rest/provider/ProviderController.java:61` | `provider_create` | `criar provedor` |
| 8 | `adapters/in/rest/provider/ProviderController.java:92` | `provider_update` | `atualizar provedor` |
| 9 | `adapters/in/rest/provider/ProviderController.java:122` | `provider_get_by_id` | `buscar provedor por ID` |
| 10 | `adapters/in/rest/provider/ProviderController.java:150` | `provider_search` | `listar provedores` |
| 11 | `adapters/in/rest/provider/ProviderController.java:178` | `provider_search` | `listar provedores ativos` |
| 12 | `adapters/in/rest/provider/ProviderController.java:207` | `provider_search` | `listar provedores por tipo` |
| 13 | `adapters/in/rest/provider/ProviderController.java:239` | `provider_get_by_id` | `buscar provedor padrão` |
| 14 | `adapters/in/rest/provider/ProviderController.java:273` | `provider_delete` | `deletar provedor` |
| 15 | `adapters/in/rest/provider/ProviderController.java:300` | `provider_activate` | `ativar provedor` |
| 16 | `adapters/in/rest/provider/ProviderController.java:328` | `provider_deactivate` | `desativar provedor` |
| 17 | `adapters/in/rest/provider/ProviderController.java:357` | `provider_set_default` | `definir como padrão` |
| 18 | `adapters/in/rest/provider/ProviderController.java:383` | `provider_types` | `listar tipos de provedores` |
| 19 | `adapters/in/rest/provider/ProviderController.java:405` | `communication_types` | `listar tipos de comunicação` |
| 20 | `adapters/in/rest/message/MessageController.java:65` | `message_send` | `enviar mensagem` |

Nenhum uso de `applicationParam` em nenhuma das 20 ocorrências (sempre default,
vira `application=none` na métrica).

### 3.3 `lib-go-common/oplog` e `httpmetrics` cobrem as anotações?

- **`oplog.Logger.Run`/`RunCtx`** (`lib-go-common/oplog/operation_logger.go:74-114`)
  cobre 1:1 o que `@LogOperation` faz: incrementa `operations_total{operation,status}`,
  incrementa `business_errors_total{error_type}` na falha, e grava o evento de
  auditoria SUCCESS/FAILURE via `audit.NewOplogRecorder` quando `Audit=true`. O
  mapeamento de campos é direto: `operation`→`Operation.Name`,
  `auditAction`→`Operation.Action`, `auditEntityType`→`Operation.ResourceType`,
  `description` (já com placeholders resolvidos)→`Operation.Reason`,
  `audit`→`Operation.Audit`. **Cobre tudo que o ms-communication usa.** Único
  ponto de atenção: o Java resolve os placeholders (`{command.name}`, `{id}`,
  `{rabbitMQMessage.xCorrelationId}`) automaticamente via reflection nos
  parâmetros do método; em Go isso precisa ser montado manualmente no texto de
  `Reason` em cada chamada (não é lacuna de funcionalidade, é o trade-off
  esperado de "aspecto implícito" → "código explícito" já documentado no README
  da lib).
- **`httpmetrics.Recorder.Observe`** (`lib-go-common/httpmetrics/http_metrics_recorder.go:47-60`)
  cobre 1:1 o `@MetricsEndpoint`: `api_requests_total`/`api_requests_latency_seconds`
  com rótulos `endpoint`, `application` (vazio→`none`), `status` (`success`/`error`).
  **Cobre tudo que o ms-communication usa** (nenhum endpoint usa `applicationParam`,
  então o campo fica sempre `none`, que é o comportamento padrão do `Recorder`).

Não há lacuna de funcionalidade entre as anotações Java e os pacotes Go
equivalentes para o que o ms-communication usa.

## 4. `lib-go-common/comm` x `domain/enums` do ms-communication

`comm.go` (`lib-go-common/comm/comm.go:14-67`) espelha, valor a valor:

- `CommunicationType`: `EMAIL`, `SMS`, `PUSH_NOTIFICATION`, `WHATSAPP`,
  `TELEGRAM`, `SENDGRID`, `PUSH` — idêntico a `CommunicationTypeEnum.java:3-10`.
- `MessageType`: `EMAIL`, `SMS`, `PUSH_NOTIFICATION`, `WHATSAPP`, `PUSH` —
  idêntico a `MessageTypeEnum.java:3-8`.
- `TemplateType`: todos os 16 valores (`AUTENTICACAO_EMAIL_TOKEN` até
  `FATURA_VENCIDA`) — idêntico a `TemplateTypeEnum.java:3-19`.

**Nenhum valor faltando.** `comm` já cobre 100% dos enums compartilhados que o
ms-communication usa.

Importante: `ProviderTypeEnum` (`domain/enums/ProviderTypeEnum.java`, valores
`N8N`, `EMAIL_GOOGLE_SENDER`, `SENDGRID`, `SRV_SMS_SENDER`) **não é** um enum de
`lib-common` — é local ao `ms-communication` (`domain.enums` do próprio serviço,
não `communication.enums` da lib). Não precisa ir para `lib-go-common/comm`;
na migração vira um tipo local do serviço Go, igual é hoje.

## 5. Outras classes usadas sem equivalente em `lib-go-common` hoje

| Classe/padrão Java | Usada em | Tem equivalente em `lib-go-common`? | Proposta |
|---|---|---|---|
| `GlobalExceptionHandler` local (`infrastructure/rest/GlobalExceptionHandler.java`, 11 handlers, todos usando `ProblemDetail.forStatusAndDetail` manualmente) | Todo o módulo REST | Equivalente conceitual existe (`apperr` — ProblemDetail RFC 7807), mas este handler **não usa `lib-common`**, é código 100% local já hoje | Migrar para `lib-go-common/apperr` — é exatamente o caso de uso do pacote; o serviço ganha o formato padrão sem reescrever 11 handlers manualmente |
| `MetricsPort`/`MetricsAdapter` com API ampla (`startSample`, `incrementCounter` arbitrário, `recordTimer`, `setGauge`, `recordMessageSend`) (`infrastructure/metrics/MetricsAdapter.java:1-52`) | `TemplateCommandService`, `ProviderCommandService`, `MessageCommandService` (contadores de negócio: `template_created_total`, `provider_created_total`, `message_sent_total`, etc.) | Parcial — `httpmetrics` só cobre o padrão por-endpoint do `@MetricsEndpoint`; os contadores de negócio arbitrários (`incrementCounter` com nome e tags livres) não têm pacote na lib compartilhada | **Não** propor pacote novo em `lib-go-common` — o padrão já adotado em `ms-auth-go`/`ms-company-go` (`internal/adapters/out/metrics/business_metrics.go`, implementando `oplog.Counter`) resolve isso **local ao serviço**: contador Prometheus criado sob demanda por nome, com uma lista fixa de "known counters" registrada no boot. Replicar esse padrão local no ms-communication-go, não criar pacote compartilhado |
| `CorrelationIdFilter` + `CorrelationContext` locais (`infrastructure/filter/CorrelationIdFilter.java`, `infrastructure/context/CorrelationContext.java`) — geram/propagam `X-Correlation-ID` via MDC, ignoram paths de actuator | Todas as requisições HTTP | **Sim, já cobre** — `lib-go-common/correlation` (middleware + contexto do `X-Correlation-ID`, `Transport` para propagar em chamadas de saída) é exatamente este par de classes | Nenhuma proposta nova: é só trocar o filtro/context local pelo middleware de `correlation` na migração |
| `utils.ValidationUtils` (import morto nos 3 controllers) | Nenhum uso real | `brdoc` cobre `ValidateAndParseUUID`/e-mail equivalentes, caso volte a ser usado | Nenhuma ação — é import morto, não entra na migração como funcionalidade a portar |

Resumo do item 5: a única lacuna real (funcionalidade usada, sem pacote
compartilhado e sem padrão já resolvido) é o **contador de negócio arbitrário**
— e a decisão, seguindo o precedente de `ms-auth-go`/`ms-company-go`, é
implementá-lo local ao serviço (`internal/adapters/out/metrics`), não como novo
pacote em `lib-go-common`.
