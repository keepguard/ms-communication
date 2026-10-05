# ms-communication — Spec de regressão para migração Java → Go

Documento de especificação do comportamento ATUAL do serviço `ms-communication` (Spring Boot,
produção), para servir de base de regressão na reescrita em Go. Não contém código Go, não
corrige nada do Java — bugs/comportamentos estranhos estão isolados na seção 10.

Convenção de referência: `caminho/relativo/ao/ms-communication:linha`, caminho relativo à raiz
de `keepguard-core/backend/ms/ms-communication/`.

Documentos complementares já produzidos nesta mesma pasta (não duplicados aqui em detalhe,
apenas referenciados onde relevante):
- `02-consumidores-infra.md` — consumidores HTTP externos confirmados (ms-ai-guardian, ms-auth,
  ms-auth-go, ms-ai-guardian-go, bff-auth, bff-core), contratos fora do HTTP (Redis, RabbitMQ,
  fila `keepguard.notifications.sms` com consumidor externo `srv-sms-sender`), estado real do
  deployment K8s em produção (profile Spring ativo é `local`, não `prod`).
- `03-libs-lib-go-common.md` — uso de `lib-common` (enums, `@LogOperation`, `@MetricsEndpoint`,
  `MetricsService`), ausência de `lib-security`/`lib-validation`, comparação completa dos enums
  de comunicação com `lib-go-common/comm`.
- `04-resiliencia-integracoes.md` — timeout/retry/circuit breaker/bulkhead por client Feign,
  comportamento de ack/nack/DLQ dos 2 consumers RabbitMQ, confirmação do bug `ofDefaults()`.

---

## 1. Endpoints REST

Porta `8082` (local `8582`, `application-local.yml:2`). Base path conforme cada controller.
Nenhum endpoint exige `Authorization: Bearer` — não há `lib-security` nem JWT no código (ver
seção 8). Todos (exceto health) exigem header `X-Company-Id: UUID`, lido via
`@RequestHeader("X-Company-Id") UUID companyId` — Spring responde **400** automaticamente se o
header faltar ou não for um UUID parseável (não é um handler customizado, é o comportamento
padrão do `@RequestHeader` com tipo `UUID`).

### 1.1 Health

**GET `/api/v1/health`** — `adapters/in/rest/health/HealthController.java:55-88`.
- Sem header exigido.
- Resposta sempre `Map<String,Object>` com `service="ms-communication"`, `status` (`"UP"`/`"DOWN"`),
  `timestamp` (`LocalDateTime.now().toString()`, formato padrão Java, **não** o padrão
  `yyyy-MM-dd'T'HH:mm:ss` do Jackson customizado — este endpoint monta o Map manualmente, não
  passa pelo `ObjectMapper` de `JacksonConfig`), `components.database` (`"UP"`/`"DOWN"`).
- Status HTTP: `200` se DB ok (`HealthController.java:81-84`), `503` se `checkDatabaseHealth()`
  retornar `false` (`HealthController.java:85-87`) — conexão testada com `connection.isValid(2)`
  dentro de try-with-resources (`HealthController.java:116-123`), 2s de timeout.
- Implementa também `HealthIndicator` (`health()`, linhas 94-109) — usado por
  `/actuator/health`.

### 1.2 Mensagens — `MessageController` (`adapters/in/rest/message/MessageController.java`)

**POST `/api/v1/messages/send`** — `MessageController.java:35-91`.
- Headers: `X-Company-Id: UUID` (obrigatório, via `@RequestHeader`).
- Body: `MessageSendRequestDTO` (`adapters/in/rest/message/dto/request/MessageSendRequestDTO.java`):
  | Campo | Tipo | Validação | Mensagem de erro |
  |---|---|---|---|
  | `messageType` | `MessageTypeEnum` | `@NotNull` (linha 15) | "Tipo da mensagem é obrigatório" |
  | `recipient` | `String` | `@NotBlank` `@Size(max=200)` (linhas 18-19) | "Destinatário é obrigatório" / "Destinatário deve ter no máximo 200 caracteres" |
  | `templateType` | `TemplateTypeEnum` | `@NotNull` (linha 22) | "Tipo do template é obrigatório" |
  | `subject` | `String` | `@Size(max=200)` (linha 25) | "Assunto deve ter no máximo 200 caracteres" |
  | `content` | `String` | `@Size(max=1000)` (linha 28) | "Conteúdo deve ter no máximo 1000 caracteres" |
  | `communicationType` | `CommunicationTypeEnum` | `@NotNull` (linha 31) | "Tipo de comunicação é obrigatório" |
  | `codeUser` | `String` | `@Size(max=100)` (linha 34) | "CodeUser deve ter no máximo 100 caracteres" |
  | `variables` | `Map<String,Object>` | nenhuma | — |
  Nenhum campo tem `@Email`. Não há validação de `recipient` ser e-mail válido mesmo quando
  `communicationType=EMAIL` — aceita qualquer string de até 200 chars.
- Resposta `200 OK` SEMPRE (quando passa a Bean Validation) — `MessageSendResponseDTO`
  (`dto/response/MessageSendResponseDTO.java:9-22`): `success` (boolean) + `message` (String,
  `"Mensagem enviada com sucesso"` ou `"Falha ao enviar mensagem"`, hardcoded em
  `MessageController.java:83`). Falha de envio (provider indisponível, todos os providers
  falharam) **não** gera 4xx/5xx — vira `success=false` dentro de um 200. Só erro de validação
  Bean Validation (400) ou exceção não tratada na cadeia (500, pelo handler genérico) escapam
  desse padrão.
- **BUG (seção 10):** o `@Schema(example=...)` do Swagger em `MessageController.java:44-56` usa
  `"templateType": "WELCOME"` — valor que não existe em nenhum dos 16 valores de
  `TemplateTypeEnum`.

### 1.3 Providers — `ProviderController` (`adapters/in/rest/provider/ProviderController.java`)

Base `/api/v1/providers`. Todos exigem `X-Company-Id` no header (mas nenhum provider tem campo
`companyId` no domínio — ver seção 3; o header é lido e logado, nunca usado para filtrar).

| # | Método | Path | Request body | Response | Status sucesso |
|---|---|---|---|---|---|
| 1 | POST | `/` | `ProviderCreateRequestDTO` | `ProviderCreateResponseDTO` | 201 |
| 2 | PUT | `/{id}` | `ProviderUpdateRequestDTO` | `ProviderUpdateResponseDTO` | 200 |
| 3 | GET | `/{id}` | — | `ProviderGetProviderByIdResponseDTO` | 200 |
| 4 | GET | `/` | — | `List<ProviderGetAllProvidersResponseDTO>` | 200 |
| 5 | GET | `/active` | — | `List<ProviderGetActiveProvidersResponseDTO>` | 200 |
| 6 | GET | `/type/{communicationType}` | — | `List<ProviderGetProvidersByCommunicationTypeResponseDTO>` | 200 |
| 7 | GET | `/default/{communicationType}` | — | `ProviderGetDefaultProviderResponseDTO` | 200 |
| 8 | DELETE | `/{id}` | — | (vazio) | 204 |
| 9 | PATCH | `/{id}/activate` | — | `ProviderActivateProviderResponseDTO` | 200 |
| 10 | PATCH | `/{id}/deactivate` | — | `ProviderDeactivateProviderResponseDTO` | 200 |
| 11 | PATCH | `/{id}/default` | — | `ProviderSetAsDefaultResponseDTO` | 200 |
| 12 | GET | `/types` | — | `ProviderTypeEnum[]` (4 valores) | 200 |
| 13 | GET | `/communication-types` | — | `CommunicationTypeEnum[]` (7 valores) | 200 |

Linhas no controller: 1→43-77, 2→79-109, 3→111-137, 4→139-166, 5→168-194, 6→196-225,
7→227-260, 8→262-287, 9→289-315, 10→317-343, 11→345-372, 12→374-394, 13→396-416.

`ProviderCreateRequestDTO` (`adapters/in/rest/provider/dto/request/ProviderCreateRequestDTO.java`):
| Campo | Tipo | Validação | Default |
|---|---|---|---|
| `name` | String | `@NotBlank` (linha 19) "Nome do provedor é obrigatório" | — |
| `providerType` | `ProviderTypeEnum` | `@NotNull` (linha 25) "Tipo do provedor é obrigatório" | — |
| `communicationType` | `CommunicationTypeEnum` | `@NotNull` (linha 31) "Tipo de comunicação é obrigatório" | — |
| `isActive` | Boolean | — | `true` (linha 36) |
| `isDefault` | Boolean | — | `false` (linha 40) |
| `priority` | Integer | `@Min(1)` `@Max(10)` (linhas 44-45) | `1` |
| `url` | String | — | — |
| `configuration` | String (JSON) | — | — |
| `maxRetries` | Integer | `@Min(1)` `@Max(10)` (linhas 57-58) | `3` |
| `timeoutSeconds` | Integer | `@Min(5)` `@Max(300)` (linhas 63-64) | `30` |
| `rateLimitPerMinute` | Integer | — | — |
| `dailyLimit` | Integer | — | — |
| `monthlyLimit` | Integer | — | — |

`ProviderUpdateRequestDTO` tem as mesmas validações de `name`/`providerType`/`communicationType`/
`priority`/`maxRetries`/`timeoutSeconds` (`dto/request/ProviderUpdateRequestDTO.java:15-42`),
sem valores default (campos opcionais ficam `null` se omitidos — e `ProviderCommandService.update`
seta o domínio com esses `null`s diretamente, exceto `priority` via `updatePriority()` que ignora
`null`/negativo).

Todos os 9 Response DTOs de Provider (Create/Update/GetById/GetAll/GetActive/
GetByCommunicationType/GetDefault/Activate/Deactivate/SetAsDefault) têm exatamente os mesmos 16
campos: `id`, `name`, `providerType`, `communicationType`, `isActive`, `isDefault`, `priority`,
`url`, `configuration`, `maxRetries`, `timeoutSeconds`, `rateLimitPerMinute`, `dailyLimit`,
`monthlyLimit`, `createdAt`, `updatedAt` (formato `LocalDateTime` serializado pelo `ObjectMapper`
customizado de `JacksonConfig` como `yyyy-MM-dd'T'HH:mm:ss`, sem timezone, sem milissegundos —
mas só quando o bean customizado é de fato o `ObjectMapper` ativo do Spring MVC; ver seção 10).

`ProviderTestProviderConnectionResponseDTO` existe (`dto/response/ProviderTestProviderConnectionResponseDTO.java`)
mas **não há endpoint** no controller que o exponha — é código órfão (o `ProviderAdapterMapper`
tem `toTestProviderConnectionResponseDTO`, linha 375-387, mas nenhum controller chama).

### 1.4 Templates — `TemplateController` (`adapters/in/rest/template/TemplateController.java`)

Base `/api/v1/templates`.

| # | Método | Path | Request | Response | Status |
|---|---|---|---|---|---|
| 1 | POST | `/` | `TemplateCreateRequestDTO` | `TemplateCreateResponseDTO` | 201 |
| 2 | PUT | `/{id}` | `TemplateUpdateRequestDTO` | `TemplateUpdateResponseDTO` | 200 |
| 3 | DELETE | `/{id}` | — | (vazio) | 204 |
| 4 | GET | `/{id}` | — | `TemplateGetTemplateByIdResponseDTO` | 200 |
| 5 | GET | `/by-type` | query `type`, `messageType` | `TemplateGetTemplateByTypeResponseDTO` | 200 |
| 6 | GET | `/` | — | `List<TemplateGetTemplatesResponseDTO>` | 200 |
| 7 | GET | `/teste-hot-reload` | — | `String` | 200 |

Linhas: 1→41-75, 2→77-107, 3→109-134, 4→136-162, 5→164-192, 6→194-220, 7→223-230.

`TemplateCreateRequestDTO` (`dto/request/TemplateCreateRequestDTO.java`):
| Campo | Tipo | Validação |
|---|---|---|
| `templateType` | `TemplateTypeEnum` | `@NotNull` (linha 19) "Tipo do template é obrigatório" |
| `messageType` | `MessageTypeEnum` | `@NotNull` (linha 25) "Tipo da mensagem é obrigatório" |
| `name` | String | `@NotBlank` `@Size(max=200)` (linhas 31-32) "Nome é obrigatório" / "Nome deve ter no máximo 200 caracteres" |
| `description` | String | `@NotBlank` `@Size(max=500)` (linhas 38-39) "Descrição é obrigatória" / "Descrição deve ter no máximo 500 caracteres" |
| `subject` | String | `@Size(max=200)` (linha 44) "Assunto deve ter no máximo 200 caracteres" |
| `content` | String | `@NotBlank` (linha 50) "Conteúdo é obrigatório" |
| `variables` | String (JSON) | — |
| `companyId` | String | `@NotBlank` `@Size(max=100)` (linhas 60-61) "Aplicação é obrigatória" / "Aplicação deve ter no máximo 100 caracteres" — `@JsonProperty("companyId")` mas semanticamente é "application" (nome da aplicação cliente, não tenant) |
| `isActive` | Boolean | — |

`TemplateUpdateRequestDTO` (`dto/request/TemplateUpdateRequestDTO.java`): mesmas validações de
`name`/`description`/`content` (`@NotBlank`), `subject` (`@Size(max=200)`), `messageType`/
`templateType` (`@NotNull`); **não tem campo `companyId`/`application`** — update não permite
trocar a aplicação proprietária do template.

Response DTOs de Template (Create/Update/GetById/GetByType/GetTemplates) têm campos: `id`,
`type` (alias de `templateType`), `templateType`, `messageType`, `companyId` OU `application`
(nome do campo varia por DTO — `TemplateCreateResponseDTO`/`TemplateGetTemplatesResponseDTO`
usam `companyId` com `@JsonProperty("companyId")`; `TemplateGetTemplateByIdResponseDTO`/
`TemplateGetTemplateByTypeResponseDTO`/`TemplateUpdateResponseDTO` usam `application` sem
annotation — **inconsistência de nome de campo JSON entre endpoints do mesmo recurso**, ver
seção 10), `name`, `description`, `subject`, `content`, `variables`, `isActive`, `createdAt`,
`updatedAt`.

**BUG CRÍTICO (seção 10):** endpoint 5 (`GET /by-type`) ignora completamente os query params
`type`/`messageType` e busca por `templatePort.getById(UUID.randomUUID())`
(`TemplateController.java:188`) — nunca retorna o template certo (quase sempre 404/erro, pois o
UUID é aleatório).

Endpoint 7 (`/teste-hot-reload`, linha 223-230) é um endpoint de debug sem proteção, sem
`@MetricsEndpoint`, retornando `"Hot Reload está funcionando! Timestamp: " + System.currentTimeMillis()`.
Não deve ser portado (ver seção 10).

---

## 2. Contrato de erro — `GlobalExceptionHandler`

Arquivo `infrastructure/rest/GlobalExceptionHandler.java` (248 linhas), `@RestControllerAdvice`.
Usa `ProblemDetail.forStatusAndDetail(status, message)` + `setType`/`setTitle`/`setProperty`.

**Confirmado (leitura completa + cross-check com `GlobalExceptionHandlerTest.java`):** os campos
extras setados via `problemDetail.setProperty("timestamp", ...)` / `setProperty("path", ...)` /
`setProperty("errorCode", ...)` etc. ficam na **raiz do JSON** serializado — comportamento padrão
do Jackson mixin do Spring (`ProblemDetailJacksonMixin`) para `ProblemDetail`. Não existe um
objeto `"properties": {...}` aninhado no corpo HTTP de resposta; a API Java
`problemDetail.getProperties()` é só a representação em memória.

`timestamp`: `LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)` — formato
`yyyy-MM-ddTHH:mm:ss[.nnnnnnnnn]`, **sem timezone**, com precisão de nanossegundos variável
(diferente do formato fixo `yyyy-MM-dd'T'HH:mm:ss` do `JacksonConfig` usado nos demais DTOs).

`path`: `request.getDescription(false).replace("uri=", "")` — sempre o path da request (ex.
`/api/v1/messages/send`), nunca inclui query string (confirmado por
`shouldHandlePathExtraction`, `GlobalExceptionHandlerTest.java:309-322`).

| Exception | Status | Title | Type (URI) | errorCode | Campos extra | Linha |
|---|---|---|---|---|---|---|
| `NotFoundException` | 404 | "Recurso não encontrado" | `.../problems/not-found` | — | `timestamp`, `path` | 35-46 |
| `AlreadyExistsException` | 409 | "Recurso já existe" | `.../problems/already-exists` | — | `timestamp`, `path` | 48-59 |
| `RequiredFieldException` | 400 | "Campo obrigatório não informado" | `.../problems/required-field` | — | `timestamp`, `path` | 61-72 |
| `UnsupportedMessageTypeException` | 400 | "Tipo de mensagem não suportado" | `.../problems/unsupported-message-type` | — | `timestamp`, `path` | 74-85 |
| `ProviderNotFoundException` | 404 | "Provedor não encontrado" | `.../problems/provider-not-found` | — | `timestamp`, `path` | 87-98 |
| `MessageSendException` | 500 | "Erro ao enviar mensagem" | `.../problems/message-send-error` | — | `timestamp`, `path` | 100-111 |
| `ProviderConnectionException` | 503 | "Erro de conexão com provedor" | `.../problems/provider-connection-error` | — | `timestamp`, `path` | 113-124 |
| `MethodArgumentNotValidException` | 400 | "Erro de validação" | `.../problems/validation-error` | — | `timestamp`, `path`, `errors` (Map campo→mensagem) | 126-145 |
| `EmailSendException` | 500 | "Erro ao enviar email" | `.../problems/email-send-error` | `EMAIL_SEND_ERROR` (fixo) | `timestamp`, `path`, `errorCode` | 147-159 |
| `InvalidTemplateException` | 400 | "Template inválido" | `.../problems/invalid-template` | `INVALID_TEMPLATE` (fixo) | `timestamp`, `path`, `errorCode` | 161-173 |
| `TemplateProcessorService.TemplateNotFoundException` | 404 | "Template não encontrado" | `.../problems/template-not-found` | `TEMPLATE_NOT_FOUND` (fixo) | `timestamp`, `path`, `errorCode` | 175-187 |
| `CommandOperationException` | 500 | "Falha na operação de comando" | `.../problems/command-operation-failed` | dinâmico (`ex.getErrorCode()`) | `timestamp`, `path`, `errorCode`, `operation`, `context` | 189-203 |
| `QueryOperationException` (causa = `NotFoundException`) | **404** | "Recurso não encontrado" | `.../problems/not-found` | `RESOURCE_NOT_FOUND` (fixo) | `timestamp`, `path`, `errorCode`, `operation`, `context` | 210-221 |
| `QueryOperationException` (outra causa) | 500 | "Falha na operação de consulta" | `.../problems/query-operation-failed` | dinâmico (`ex.getErrorCode()`) | `timestamp`, `path`, `errorCode`, `operation`, `context` | 223-234 |
| `Exception` (genérica) | 500 | "Erro interno do servidor" | `.../problems/internal-server-error` | — (sem errorCode) | `timestamp`, `path` | 236-247 |

Nenhum handler usa placeholder `{...}` literal não interpolado — todas as mensagens são
concatenação Java simples ou `ex.getMessage()` direto.

**Importante:** `RuntimeException` genérica (lançada por `ProviderController.getProviderById`/
`ProviderController.getDefaultProvider` [via `.orElseThrow`]/`TemplateController.getTemplateById`/
`TemplateController.getTemplateByType` quando o recurso não existe, com mensagem literal
`"Provider not found"`/`"Template not found"`) **não é nenhuma das exceções mapeadas** — cai no
handler genérico `Exception.class` → sempre **500**, nunca 404, apesar da intenção óbvia ser 404.
Ver seção 10.

---

## 3. Domínio

### 3.1 Entidade `Provider` (`domain/entity/Provider.java`)

Classe mutável (getters/setters públicos, sem Lombok — construtores e getters/setters escritos à
mão, linhas 32-168). **Não tem campo de tenant/companyId** — é uma entidade global, não escopada
por empresa, apesar de todos os endpoints de `ProviderController` exigirem `X-Company-Id` no
header (nunca usado para filtrar).

Campos: `id` (UUID), `name`, `providerType` (`ProviderTypeEnum`), `communicationType`
(`CommunicationTypeEnum`), `isActive` (Boolean, default `true`), `isDefault` (Boolean, default
`false`), `priority` (Integer, default `1`), `url`, `configuration` (String JSON), `maxRetries`
(Integer, default `3`), `timeoutSeconds` (Integer, default `30`), `rateLimitPerMinute`,
`dailyLimit`, `monthlyLimit` (todos Integer, sem default), `variables` (`Map<String,Object>`,
nunca populado por nenhum fluxo real — DTOs de criação/atualização não têm campo `variables`
para Provider), `createdAt`, `updatedAt`.

Factory `Provider.create(name, providerType, communicationType, url, configuration)`
(`Provider.java:59-81`): seta `isActive=true`, `isDefault=false`, `priority=1`, `maxRetries=3`,
`timeoutSeconds=30`, demais campos `null`, timestamps = `now()`.

Invariantes/transições:
- `activate()`/`deactivate()` (linhas 83-91): togglam `isActive`, atualizam `updatedAt`. Sem
  checagem de pré-condição (pode ativar um já ativo).
- `setAsDefault()`/`unsetAsDefault()` (linhas 93-101): togglam `isDefault`. **Nenhuma invariante
  de "só um default por tipo de comunicação" é garantida na entidade** — a garantia (se existir)
  teria que vir do service/repository (não há — `ProviderCommandService.setAsDefault` só chama
  `provider.setAsDefault()` e salva, sem desmarcar outros providers do mesmo tipo).
- `updatePriority(Integer)` (linhas 103-108): ignora silenciosamente `null` ou `<= 0`.
- `equals`/`hashCode`/`toString`: não declarados explicitamente no código lido — herdam de
  `Object` (identidade de referência), confirmado pelo teste `shouldImplementEqualsCorrectly`
  (`ProviderTest.java:240-262`, que espera `provider1 != provider2` mesmo com dados idênticos
  porque `Provider.create` gera objetos diferentes — na verdade via `@Builder` do Lombok no
  topo da classe, que a classe usa só para o builder, não para equals/hashCode/toString, que são
  overrides manuais vistos nas linhas 170-191 do arquivo, usando apenas campos, SEM comparação de
  identidade declarada — ou seja, dois `Provider` com os mesmos valores em todos os campos SÃO
  diferentes por `equals` porque a classe não sobrescreve `equals`/`hashCode`, apesar do
  `toString` customizado existir).

### 3.2 Entidade `Template` (`domain/entity/Template.java`)

Também mutável, sem Lombok. **Tem** campo de tenant: `companyId` (String — nome de aplicação
cliente, ex. `"ms-auth"`, não um UUID de empresa real, apesar do nome).

Campos: `id`, `templateType` (`TemplateTypeEnum`), `messageType` (`MessageTypeEnum`), `companyId`
(String), `name`, `description`, `subject`, `content`, `variables` (String — **JSON stringificado,
não `Map`**, diferente de `Provider.variables`), `isActive` (default `true`), `createdAt`,
`updatedAt`.

Factory `Template.create(templateType, messageType, companyId, name, description, subject, content)`
(`Template.java:47-65`): gera `id = UUID.randomUUID()` (diferente de `Provider.create`, que deixa
`id=null` para o JPA gerar), `variables = "[]"` default, `isActive=true`.

Invariantes:
- `activate()`/`deactivate()` (linhas 67-75): mesmo padrão de `Provider`.
- `updateContent(String)` (linhas 77-82): ignora `null`/vazio silenciosamente.
- `updateSubject(String)` (linhas 84-87): **sem** essa proteção — aceita `null` direto.
- `updateVariables(String)` (linhas 89-92): sem proteção, aceita qualquer string incluindo `null`.
- `hasVariables()` (linhas 98-100): `variables != null && !variables.isEmpty()`.

### 3.3 Enums compartilhados (`lib-common` / `lib-go-common/comm`)

Comparação já feita em sessão anterior e confirmada nesta leitura (arquivos relidos
integralmente):

**`CommunicationTypeEnum`** — `lib-common/src/main/java/com/keepguard/lib_common/communication/enums/CommunicationTypeEnum.java:3-10`
(7 valores): `EMAIL`, `SMS`, `PUSH_NOTIFICATION`, `WHATSAPP`, `TELEGRAM`, `SENDGRID`, `PUSH`.
Idêntico a `lib-go-common/comm/comm.go:16-24` (`CommEmail`...`CommPush`).

**`MessageTypeEnum`** — `lib-common/.../MessageTypeEnum.java:3-8` (5 valores): `EMAIL`, `SMS`,
`PUSH_NOTIFICATION`, `WHATSAPP`, `PUSH`. Idêntico a `comm.go:34-40`.

**`TemplateTypeEnum`** — `lib-common/.../TemplateTypeEnum.java:3-19` (16 valores):
`AUTENTICACAO_EMAIL_TOKEN`, `AUTENTICACAO_EMAIL_TOKEN_RESEND`, `AUTENTICACAO_SMS_TOKEN`,
`AUTENTICACAO_WHATSAPP_TOKEN`, `AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN`,
`AUTENTICACAO_DISPOSITIVO_SMS_TOKEN`, `AUTENTICACAO_DISPOSITIVO_WHATSAPP_TOKEN`,
`CADASTRO_SUCESSO`, `RECUPERACAO_SENHA`, `SENHA_ALTERADA_SUCESSO`, `NOVO_DISPOSITIVO_AUTENTICADO`,
`NOTIFICACAO_GERAL`, `ALERTA_SEGURANCA`, `CONFIRMACAO_ACAO`, `FATURA_PAGA`, `FATURA_VENCIDA`.
Idêntico a `comm.go:50-67` (mesmos nomes, mesmo `getValue()`/`Value()` em minúsculo via
`toLower`). **Nenhum valor faltando em nenhum dos 3 enums.**

**`ProviderTypeEnum`** (`domain/enums/ProviderTypeEnum.java:3-7`, 4 valores): `N8N` (display
`"n8n"`), `EMAIL_GOOGLE_SENDER` (`"srv-email-google-sender"`), `SENDGRID` (`"sendgrid"`),
`SRV_SMS_SENDER` (`"srv-sms-sender"`). **Não existe em `lib-common` nem em `lib-go-common/comm`**
— é específico deste serviço. Gap de migração: na versão Go, vira um tipo local do serviço
(`type ProviderType string` com as mesmas 4 constantes), não entra em `lib-go-common`.

---

## 4. Use cases — passo a passo dos services de application

### 4.1 `MessageCommandService` (`application/service/message/MessageCommandService.java`)

Não tem `@Transactional` na classe (service stateless, sem persistência própria). Depende de
`ProviderRepositoryPort`, `ProviderStrategyFactory`, `MetricsPort`, `List<CommunicationProvider>`
(injeção de todos os beans que implementam a interface), `TemplateProcessorService`.

**`sendWithProvider(UUID providerId, MessageSendCommandDTO command)`** (linhas 43-76):
1. `@LogOperation(operation="SEND_MESSAGE_WITH_PROVIDER", description="Enviando mensagem via provedor: {providerId} para: {command.recipient}", audit=true, auditAction="SEND_MESSAGE", auditEntityType="MESSAGE")`.
2. Busca `Provider` por ID; se não existe, `NotFoundException("Provedor não encontrado: " + providerId, "PROVIDER_NOT_FOUND", Map.of("providerId", providerId))`.
3. Chama `sendViaProvider(provider, command)` (privado, compartilhado com `sendWithFallback`).
4. Métrica `message_sent_total` incrementada com tags `provider_id`, `type` (communicationType),
   `status` (`SUCCESS`/`FAILED`/`ERROR`) — linhas 54-72.
5. Em `catch (Exception e)`: métrica `ERROR`, loga, **relança** (`throw e`) — este método propaga
   exceções, diferente de `sendWithFallback`.

**`sendWithFallback(MessageSendCommandDTO command)`** (linhas 85-117) — é o que
`MessageController`/`MessageRabbitMQProcessorService`/`BillingInvoiceFactRabbitMQConsumer` usam:
1. `@LogOperation(operation="SEND_MESSAGE_WITH_FALLBACK", ...)`.
2. Busca `List<Provider>` ativos por `command.getCommunicationType()` via
   `providerRepositoryPort.findByCommunicationType` (query JPA já filtra `isActive=true`,
   ordenada por `priority ASC`).
3. Se lista vazia → `throw new NotFoundException("Nenhum provedor ativo encontrado para tipo: " + ...)`
   (usando o construtor de 1 arg — `errorCode` fica fixo `"NOT_FOUND"`, não
   `"PROVIDER_NOT_FOUND"` como em `sendWithProvider`).
4. Itera providers em ordem de prioridade; para cada, chama `sendViaProvider`; se `true`, retorna
   `true` imediatamente (primeiro sucesso ganha). Qualquer `Exception` de um provider é logada
   como `warn` e a iteração continua para o próximo.
5. Se todos falharem, retorna `false` (não lança exceção) — **método nunca lança** exceto na
   checagem inicial de lista vazia.

**`sendViaProvider(Provider, MessageSendCommandDTO)`** (privado, linhas 119-187) — núcleo
compartilhado:
1. Se `command.getTemplateType() != null && variables não vazias`: tenta processar template via
   `templateProcessorService.processTemplate(TemplateTypeEnum.valueOf(templateType), messageType,
   command.getCompanyId().toString(), variables)`. `messageType` default `EMAIL` se
   `command.getMessageType()` for `null`. Se o processamento falhar (template não encontrado,
   etc.), o `catch (Exception e)` apenas loga `warn` e **continua com `subject`/`content`
   originais do command** — falha de template nunca impede o envio.
2. Resolve `ProviderStrategy` via `strategyFactory.getStrategy(provider.getProviderType())`; se
   vazio, `throw new MessageSendException(...)`.
3. Resolve `CommunicationProvider` concreto via `strategy.getCommunicationProvider(communicationProviders)`
   (filtra a lista injetada pelo `supports()` da estratégia); se `null`,
   `throw new MessageSendException(...)`.
4. Chama `communicationProvider.sendMessage(provider, recipient, finalSubject, finalContent,
   communicationType, messageType, templateType)` — retorna o `boolean` que sobe a cadeia.

### 4.2 `MessageRabbitMQProcessorService` (`application/service/message/MessageRabbitMQProcessorService.java`)

Único método: `processMessageSend(MessageSendRabbitMQDTO)` (linhas 28-52), chamado pelo consumer
RabbitMQ via a porta `MessageSendRabbitMQPort`.
1. `@LogOperation(operation="PROCESS_RABBITMQ_MESSAGE_SEND", description="...xCorrelationId: {rabbitMQMessage.xCorrelationId}, recipient: {rabbitMQMessage.recipient}", audit=false, auditAction="PROCESS_MESSAGE_SEND", auditEntityType="MESSAGE")`
   — é o único `@LogOperation` do serviço com `audit=false`.
2. Mapeia para `MessageSendCommandDTO` via `MessageRabbitMQMapper.toSendCommand`.
3. Chama `messagePort.sendWithFallback(command)` — **ignora o retorno** (nem usa `boolean sent`
   nem relança nada baseado nele).
4. `catch (IllegalArgumentException e)`: loga `warn`, método retorna normalmente (sem relançar).
5. `catch (Exception e)`: loga `error`, método retorna normalmente (sem relançar).

**Consequência confirmada por teste** (`MessageRabbitMQProcessorServiceTest.shouldProcessRabbitMQMessageEvenWhenSendingFails`,
linhas 88-102): mesmo quando `sendWithFallback` retorna `false` (todos os providers falharam), o
método **não propaga nenhum sinal de erro** — retorna void silenciosamente. Como consequência,
`MessageSendRabbitMQConsumer.consumeMessageSend` (que chama esta porta) sempre vê sucesso (nenhuma
exceção sobe) e sempre faz `channel.basicAck` — **mensagens cujo envio efetivamente falhou (sem
provider disponível) são tratadas como processadas com sucesso pelo RabbitMQ**, nunca vão para
retry nem DLQ. Ver seção 10. Todos os testes desta classe confirmam
`verifyNoInteractions(eventPublisherPort)` em todo cenário — `EventPublisherPort`/
`MessageSentEvent`/`MessageFailedEvent` nunca são usados por este fluxo (nem por nenhum outro
fluxo do serviço — ver seção 10, "eventos de domínio não publicados").

### 4.3 `ProviderCommandService` (`application/service/provider/ProviderCommandService.java`)

`@Transactional` na classe (linha 24). Depende de `ProviderRepositoryPort`,
`ProviderApplicationMapper`, `MetricsPort`. **Não injeta `ProviderCachePort`** — nenhuma operação
de escrita invalida/escreve cache.

- **`create(ProviderCreateCommandDTO)`** (linhas 38-89): `@LogOperation(CREATE_PROVIDER, "Criando novo provedor: {command.name}", audit=true, CREATE, PROVIDER)`. Valida unicidade de nome via `existsByName` → `AlreadyExistsException`. Monta `Provider` via factory + setters condicionais (`if (campo != null) provider.setX(...)`) para os 8 campos opcionais. Salva. Métrica `provider_created_total{entity_id, type}`.
- **`update(UUID, ProviderUpdateCommandDTO)`** (linhas 91-133): `@LogOperation(UPDATE_PROVIDER, "Atualizando provedor: {id}", ..., UPDATE, PROVIDER)`. Busca existente (`NotFoundException` com construtor de 1 arg se ausente). Valida unicidade de nome via `existsByNameAndIdNot` → `AlreadyExistsException`. **Seta TODOS os campos diretamente sem checagem de null** (`existingProvider.setName(command.getName())` etc., linhas 111-123) — diferente de `create`, que condiciona. Isso significa que um `ProviderUpdateCommandDTO` com campos `null` (vindo de um PUT parcial) **sobrescreve** o valor existente com `null` em todos os campos exceto `priority` (que usa `updatePriority()`, que ignora `null`). Métrica `provider_updated_total`.
- **`delete(UUID)`** (linhas 135-155): `@LogOperation(DELETE_PROVIDER, ...)`. Checa existência (`NotFoundException`). Deleta físico via `deleteById`. Métrica `provider_deleted_total`. **Nenhuma checagem de "provider ativo não pode ser deletado"** apesar do Swagger do controller documentar isso (`@ApiResponse` 400 "Provedor ativo não pode ser deletado" em `ProviderController.java:270` — nunca implementado, ver seção 10).
- **`activate(UUID)`/`deactivate(UUID)`** (linhas 157-195): busca, chama `provider.activate()`/`deactivate()`, salva, retorna view. Sem métrica de negócio (diferente de create/update/delete).
- **`setAsDefault(UUID)`** (linhas 197-215): busca, chama `provider.setAsDefault()`, salva. **Não desmarca outros providers do mesmo `communicationType` que já eram default** — múltiplos providers podem ficar com `isDefault=true` simultaneamente para o mesmo tipo (ver seção 10).

### 4.4 `ProviderQueryService` (`application/service/provider/ProviderQueryService.java`)

`@Transactional(readOnly = true)` na classe. Sem cache (`ProviderCachePort` não injetado).
- `getById` (linhas 30-36): `NotFoundException` com construtor de 1 arg se ausente.
- `search`, `getAllActive`, `getByCommunicationType`, `getDefaultByCommunicationType`,
  `existsById`, `existsByName`: delegam 1:1 ao `ProviderRepositoryPort` + mapeiam via
  `ProviderApplicationMapper.toView`.

`ProviderUseCaseService` (`application/service/provider/ProviderUseCaseService.java`) é o
adapter que implementa `ProviderPort`, delegando para `ProviderCommandService`/
`ProviderQueryService`. Pontos notáveis (ver também seção 10):
- `getById` (linhas 37-44): `try { return Optional.of(queryService.getById(id)); } catch (Exception e) { return Optional.empty(); }` — qualquer exceção (incluindo erro de infraestrutura real, não só "não encontrado") é engolida e convertida em `Optional.empty()`.
- `getDefaultByCommunicationType` (linhas 63-69): mesmo padrão, mas com um bug adicional — ver seção 10 (NPE mascarado).

### 4.5 `TemplateCommandService` / `TemplateQueryService` / `TemplateUseCaseService`

Estrutura espelhada de Provider (`@Transactional` na classe de Command, `readOnly=true` na de
Query). `TemplateCommandService.activate`/`deactivate` (linhas 141-177) retornam `void` (não
`TemplateViewDTO` como em Provider) — `TemplateUseCaseService.activate`/`deactivate` (linhas
74-83) compensam fazendo uma segunda chamada: `commandService.activate(id); return
queryService.getById(id);` — ou seja, **ativar/desativar um template faz 2 operações separadas**
(1 write + 1 read), sem transação única cobrindo ambas (a `@Transactional` do command service
cobre só o `activate`/`deactivate`, não a leitura seguinte) — ver seção 10 (race condition
teórica entre as duas chamadas, embora de baixo impacto prático).

`TemplateUseCaseService.getById` (linhas 38-45) tem o mesmo padrão de engolir exceção genérica
visto em Provider.

### 4.6 `TemplateProcessorService` (`application/service/template/TemplateProcessorService.java`)

`@Transactional(readOnly = true)` na classe. Único método público:
`processTemplate(TemplateTypeEnum, MessageTypeEnum, String companyId, Map<String,Object> variables)`
(linhas 35-63):
1. `@LogOperation(operation="PROCESS_TEMPLATE", description="Processando template: {templateType} com variáveis: {variables}", audit=true, PROCESS, TEMPLATE)`.
2. Busca template ativo via `templateRepositoryPort.findByTemplateTypeAndMessageTypeAndApplicationAndIsActive(templateType, messageType, companyId, true)`.
3. Se vazio: `throw new TemplateNotFoundException(...)` (classe interna estática,
   `TemplateProcessorService.java:143-147`, mapeada no `GlobalExceptionHandler` para 404 com
   `errorCode=TEMPLATE_NOT_FOUND`).
4. Interpola `{{variavel}}` em `subject`/`content` via regex `\{\{([^}]+)\}\}`
   (`VARIABLE_PATTERN`, linha 26) e `Matcher.appendReplacement`. Variável não encontrada no map:
   mantém o placeholder original (`"{{" + variableName + "}}"`) e loga `warn` — não remove, não
   substitui por vazio.
5. Retorna `ProcessedTemplate` (classe interna com builder manual, sem Lombok) com `subject`,
   `content`, `templateId`.

Esta classe **não tem cache** (`TemplateCachePort` não injetado) — toda chamada bate no banco.

---

## 5. Persistência

PostgreSQL, schema `ms_communication` (`application.yml:42`, `hibernate.default_schema`).
`ddl-auto`: `update` em local/dev (`application.yml:37`, `application-dev.yml:33`,
`application-local.yml:30`), `validate` em prod (`application-prod.yml:32`) — **mas** ver
`02-consumidores-infra.md` seção 4: o profile real ativo em produção hoje é `local`, não `prod`,
então `ddl-auto` efetivo em produção é `update`, não `validate`. Sem Flyway/Liquibase — schema
gerenciado só por Hibernate.

### 5.1 Tabela `providers` (`infrastructure/persistence/entity/ProviderJpaEntity.java`)

| Coluna | Tipo Java | Tipo SQL | Constraints |
|---|---|---|---|
| `id` | UUID | `uuid` | PK, `GenerationType.UUID`, `updatable=false` |
| `name` | String | `varchar(100)` | `NOT NULL` |
| `provider_type` | `ProviderTypeEnum` | `varchar(50)` | `NOT NULL`, `@Enumerated(STRING)` |
| `communication_type` | `CommunicationTypeEnum` | `varchar(30)` | `NOT NULL`, `@Enumerated(STRING)` |
| `is_active` | Boolean | — | `NOT NULL`, default Java `true` |
| `is_default` | Boolean | — | `NOT NULL`, default Java `false` |
| `priority` | Integer | — | `NOT NULL`, default Java `1` |
| `url` | String | `varchar(500)` | nullable |
| `configuration` | String | `TEXT` | nullable |
| `max_retries` | Integer | — | default Java `3` |
| `timeout_seconds` | Integer | — | default Java `30` |
| `rate_limit_per_minute` | Integer | — | nullable |
| `daily_limit` | Integer | — | nullable |
| `monthly_limit` | Integer | — | nullable |
| `variables` | `Map<String,Object>` | `JSONB` | nullable, `@JdbcTypeCode(SqlTypes.JSON)` |
| `created_at` | LocalDateTime | — | `NOT NULL`, setado em `@PrePersist` |
| `updated_at` | LocalDateTime | — | `NOT NULL`, setado em `@PrePersist`/`@PreUpdate` |

Sem coluna de tenant/`company_id` — confirma entidade global (seção 3.1).

Queries customizadas (`infrastructure/persistence/spring/ProviderSpringRepository.java`):
- `findActiveProvidersByCommunicationType` (linha 19-20): `WHERE communicationType = :x AND isActive = true ORDER BY priority ASC`.
- `findAllActiveProviders` (22-23): `WHERE isActive = true ORDER BY priority ASC`.
- `findDefaultProviderByCommunicationType` (25-26): `WHERE communicationType = :x AND isDefault = true AND isActive = true`.
- `existsByName`, `existsByNameAndIdNot`: derivadas pelo Spring Data.
- `findWithFilters` (32-45): query JPQL com todos os filtros opcionais via `:param IS NULL OR campo = :param`, paginada. **Os parâmetros `providerType`/`communicationType` são recebidos como `String`** no método (`@Param("providerType") String providerType`) comparados contra colunas enum (`p.providerType = :providerType`) — o adapter (`ProviderRepositoryAdapter.search`, linha 54-55) converte via `.name()` antes de passar. Ordenação: `Sort.by(Sort.Direction.fromString(criteria.sortDirection()), criteria.sortBy())` — lança `IllegalArgumentException` se `sortBy` for `null` (não há fallback para um campo default).

### 5.2 Tabela `templates` (`infrastructure/persistence/entity/TemplateJpaEntity.java`)

| Coluna | Tipo Java | Tipo SQL | Constraints |
|---|---|---|---|
| `id` | UUID | `uuid` | PK, `GenerationType.UUID` |
| `template_type` | `TemplateTypeEnum` | `varchar(50)` | `NOT NULL` |
| `message_type` | `MessageTypeEnum` | `varchar(20)` | `NOT NULL` |
| `tenant_id` | String | `varchar(100)` | `NOT NULL` — **nome de coluna SQL é `tenant_id`, mas o campo Java é `companyId`** (mapeado via `@Column(name="tenant_id")`, linha 31) |
| `name` | String | `varchar(200)` | `NOT NULL` |
| `description` | String | `varchar(500)` | `NOT NULL` |
| `subject` | String | `varchar(200)` | nullable |
| `content` | String | `TEXT` | `NOT NULL` |
| `variables` | String | `TEXT` | nullable (JSON stringificado, não JSONB) |
| `is_active` | Boolean | — | `NOT NULL`, default `true` |
| `created_at`/`updated_at` | LocalDateTime | — | nullable na entity (sem `nullable=false`, diferente de Provider), setados em `@PrePersist`/`@PreUpdate` |

Queries (`TemplateSpringRepository.java`):
- `findActiveTemplatesByType`/`findActiveTemplatesByMessageType`/`findAllActiveTemplates`: filtram `isActive=true`.
- `findWithFilters` (33-44): mesma estrutura de filtros opcionais do Provider.
- **Dois métodos idênticos** (seção 10): `findByTemplateTypeAndMessageTypeAndTenantIdAndIsActive` (linhas 46-56, **nunca chamado** — confirmado por grep em todo `src/`) e `findTemplateByTypeMessageAndApp` (linhas 58-69, usado pelo adapter) — mesma query JPQL, nomes diferentes, comentário no código (linha 58) explica que o segundo existe "para evitar conflito com geração automática do Spring Data JPA", mas o primeiro nunca foi removido.

Nenhum `@Index` declarado em nenhuma entidade (índices, se existirem, vêm só de PK/FK implícitas
do Hibernate `ddl-auto`, não de anotação explícita).

---

## 6. Cache

Biblioteca: Spring Data Redis (`StringRedisTemplate`), serialização manual via `ObjectMapper`
injetado (não é o bean customizado do `JacksonConfig` necessariamente — é o `ObjectMapper`
padrão do Spring Boot autoconfigurado, a menos que o bean de `JacksonConfig` seja o primário;
ver seção 10).

**ACHADO CRÍTICO: todo o cache Redis de Provider/Template é código morto.** Confirmado por grep
exaustivo (`grep -rln "ProviderCachePort\|TemplateCachePort" src/main/java`): os únicos arquivos
que referenciam essas portas são a própria interface e o próprio adapter Redis
(`ProviderCacheService.java`, `TemplateCacheService.java`). **Nenhum service de application**
(`ProviderCommandService`, `ProviderQueryService`, `TemplateCommandService`,
`TemplateQueryService`, `TemplateProcessorService`) injeta `ProviderCachePort`/`TemplateCachePort`.
O `system-design.md` (seção 2) descreve "Redis para cache de Template/Provider (TTL 7 dias)" como
se estivesse em uso — não está. Ver seção 10.

Apesar de morto, o contrato implementado (para referência, caso a migração decida reativá-lo ou
documentar a intenção original):

### 6.1 `ProviderCacheService` (`infrastructure/redis/ProviderCacheService.java`)

- Chave individual: `{prefix}:{providerId normalizado}` — prefix configurável
  (`cache.redis.prefix.provider`, default `"provider_cache"`), `providerId` normalizado via
  `.trim().toLowerCase()` (linha 172-174). TTL `cache.redis.ttl.provider`, default `604800`
  (7 dias, `application.yml:57-58`).
- Chave por tipo: `{prefixByType}:{communicationType.name()}` — prefix
  `cache.redis.prefix.providers-by-type`, default `"providers_by_type"`. TTL
  `cache.redis.ttl.providers-by-type`, default `604800`.
- Valor serializado: JSON de `ProviderCacheViewDTO` (16 campos, mesmos de `ProviderViewDTO` —
  exemplo: `{"id":"...","name":"N8N Email","providerType":"N8N","communicationType":"EMAIL","isActive":true,"isDefault":false,"priority":1,"url":"https://...","configuration":"{...}","maxRetries":3,"timeoutSeconds":30,"rateLimitPerMinute":60,"dailyLimit":1000,"monthlyLimit":30000,"createdAt":"2024-01-15T10:30:00","updatedAt":"2024-01-15T10:30:00"}`).
- `@CircuitBreaker(name="redisCache")` em todos os métodos; `@Retry(name="redisCache")` só em
  `getProviderByIdFromCache`/`getProvidersByTypeFromCache`. Fallback de leitura: retorna `null`
  silenciosamente (`getProviderFallback`/`getProvidersListFallback`, log `warn`). Fallback de
  escrita/remoção: `catch (Exception e)` interno com log `warn`, nunca propaga.
- Invalidação: nunca chamada (nenhum caller) — mas os métodos `removeProviderFromCacheById`/
  `removeProvidersByTypeFromCache`/`clearAllProviderCache` existem e funcionariam se chamados.

### 6.2 `TemplateCacheService` (`infrastructure/redis/TemplateCacheService.java`)

- Chave: `{prefix}:{templateType.name()}:{messageType.name()}:{application normalizado}` —
  prefix `cache.redis.prefix.template`, default `"template_cache"`. TTL
  `cache.redis.ttl.template`, default `604800`.
- Valor: JSON de `TemplateCacheViewDTO` (12 campos). Mesmo padrão de circuit breaker/retry/
  fallback do Provider.

---

## 7. Integrações de saída

Ver `04-resiliencia-integracoes.md` para a análise completa de timeout/retry/CB/bulkhead por
client. Resumo aqui:

### 7.1 Feign clients

| Client | URL base | Timeout | Retry | Circuit Breaker | Bulkhead |
|---|---|---|---|---|---|
| `BillingUserClient` | `${USER_SERVICE_URL:http://localhost:8085}` | Nenhum explícito (default Feign) | Nenhum | Nenhum | Nenhum |
| `DynamicEmailSenderClient` | dinâmica por provider (`http://email-placeholder.local` é só placeholder) | `Request.Options(connect=10s, read=30s)` via Feign nativo | Nenhum (`@Retry`) | Nenhum | Nenhum |
| `N8nWebhookClient` | dinâmica por tenant (`http://n8n-placeholder.local` placeholder) | `Request.Options(10s/30s)` | `@Retry(name="n8nClient")` presente no adapter, mas... | `@CircuitBreaker(name="n8nClient")` presente, mas... | **Nenhum** (`@Bulkhead` não existe no código, apesar de configurado no YAML) |

### 7.2 Bug `ofDefaults()` confirmado

`infrastructure/config/resilience/ResilienceConfig.java:26-69` cria manualmente os 5 registries
(`CircuitBreakerRegistry`, `RetryRegistry`, `BulkheadRegistry`, `RateLimiterRegistry`,
`TimeLimiterRegistry`) via `XxxRegistry.ofDefaults()`. O pom.xml (linhas 119-153) inclui
`resilience4j-spring-boot3`, cuja auto-configuração normalmente leria
`resilience4j.circuitbreaker.configs`/`.instances` etc. do `application.yml:141-220` — mas essa
auto-configuração é `@ConditionalOnMissingBean`, e como os 5 beans já existem (declarados
manualmente com `ofDefaults()`), a leitura do YAML nunca ocorre. Nomes configurados no YAML
(`n8nClient`, `redisCache`, `rabbitMQProducer`, `databaseOperation`, `rabbitMQConsumer`,
`rabbitMQMessageProcessor`) existem só no papel — em runtime, quando uma anotação
`@CircuitBreaker(name="n8nClient")` pede essa instância ao registry, ela é criada on-the-fly com
os defaults puros da biblioteca Resilience4j (circuit breaker: sliding window 100, não 10;
retry: 3 tentativas com wait fixo 500ms, sem o backoff exponencial `x2` configurado). Mesmo bug
já catalogado no `ms-auth` (Java). Ver `04-resiliencia-integracoes.md` seção 1.4 para a análise
completa com os valores exatos default-vs-configurado.

### 7.3 RabbitMQ — produção

Exchanges/filas (nomes variam por profile — `.dev`/`.prod`, mas ver `02-consumidores-infra.md`
seção 4: o profile real ativo em produção hoje usa os nomes `.dev`):

- **`message.send`**: fila `${rabbitmq.queues.message-send}` (`ms.communication.message.send.dev`
  no profile dev/local; `.prod` no arquivo de prod), consumida por
  `MessageSendRabbitMQConsumer`. Producer: `RabbitMQProducerConfig.messageSendRequestsQueue`
  com DLX apontando para `deadLetterExchange`/routing key `"failed"`.
- **`billing.invoice.{paid,overdue}`**: exchange `${rabbitmq.billing-exchange}`
  (`ms-billing-exchange-*`), fila `${rabbitmq.queues.billing-invoice}`
  (`ms.communication.billing.invoice.*`), consumida por `BillingInvoiceFactRabbitMQConsumer`.
  Bindings para as routing keys `billing.invoice.paid` e `billing.invoice.overdue`
  (`BillingInvoiceRabbitConfig.java:40-51`). Publicado só pelo `ms-billing` (confirmado em
  `02-consumidores-infra.md`).
- **`message.sent`/`message.failed`**: publicados por `RabbitMQEventPublisherAdapter` no
  exchange `${rabbitmq.queues.message-exchange}` — **mas nunca chamado por nenhum fluxo real**
  (ver seção 10, "eventos de domínio não publicados").
- **Exchange `srv-email-google-sender-exchange-*`** / routing key `email.google.send`: publicado
  por `EmailSenderRabbitMQProducer.publishEmailMessage` (chamado por
  `GoogleEmailSenderCommunicationProvider.sendMessage`, caminho principal do provider
  `EMAIL_GOOGLE_SENDER`). Formato `EmailMessageDTO` (snake_case obrigatório: `company_id`,
  `x_correlation_id`, `correlationId` [sem snake_case, propositalmente duplicado], `to`,
  `subject`, `html`, `cc`, `reply_to`).
- **Fila `keepguard.notifications.sms`** (fixa, sem exchange — publicada direto via default
  exchange `""`): publicada por `SmsSenderRabbitMQProducer.publishSmsMessage` (chamado por
  `SmsSenderCommunicationProvider.sendMessage`). **Contrato externo confirmado** — consumida pelo
  `srv-sms-sender` (serviço Go externo). Payload `SmsQueueMessageDTO`: `id`, `companyId`,
  `recipient`, `body`, `senderId`, `traceId`, `correlationId`. **Esta fila precisa ser preservada
  byte a byte na migração** (nome exato, sem DLX nos args, campo `companyId` — não `tenantId`) —
  ver `02-consumidores-infra.md` seção 2, achado crítico.
- **DLQ**: `${rabbitmq.queues.message-send-requests-dlt}`, TTL 7 dias
  (`x-message-ttl: 604800000`, `RabbitMQProducerConfig.java:124-127`).

### 7.4 Consumers — idempotência e ack/nack

Ver seção 4.2 para `MessageRabbitMQProcessorService` (o processor que o consumer chama). No nível
do consumer (`MessageSendRabbitMQConsumer.java`):
- Ack manual só após `processMessageSend` retornar sem exceção (linha 54) — mas como visto na
  seção 4.2, esse método nunca lança mesmo quando o envio falhou de fato.
- `IllegalArgumentException` (erro de validação de `isValid()`): `nack(deliveryTag, false, false)`
  → direto para DLQ, sem relançar.
- Qualquer outra exceção: `nack(deliveryTag, false, true)` → requeue, relança a exceção (aciona
  `@CircuitBreaker`/`@Retry` do `rabbitMQMessageProcessor`, sujeito ao mesmo bug da seção 7.2).
- **Sem chave de dedupe** — nenhuma verificação de idempotência neste consumer (diferente do de
  billing).

`BillingInvoiceFactRabbitMQConsumer.java` (seção já detalhada no prompt original, confirmada por
releitura): idempotência via Redis `setIfAbsent` na chave `billing:email:{paid|overdue}:{invoiceId}`,
TTL `billing.email.idempotency-ttl-hours` (default 168h). Em falha após reservar a chave
(e-mail não resolvido, `sendWithFallback=false`, ou exceção), a chave é removida para permitir
reentrega — padrão "reserva otimista com rollback".

---

## 8. Infra transversal

### 8.1 Correlation ID

`infrastructure/filter/CorrelationIdFilter.java` (`OncePerRequestFilter`): lê
`X-Correlation-ID` do header de entrada, gera `UUID.randomUUID()` se ausente, seta no
`CorrelationContext` (MDC key `"correlationId"`) e devolve no header de resposta. Ignora paths
`/actuator/{prometheus,health,info,metrics}` (não gera log nem correlation para esses). Limpa o
MDC no `finally`. No consumer RabbitMQ, o correlation vem do próprio payload
(`rabbitMQMessage.xCorrelationId()`) e é posto manualmente no MDC
(`MessageSendRabbitMQConsumer.java:40`), junto com `companyId`/`X-Tenant-Id`.

### 8.2 Logging

`logback-spring.xml`: 3 appenders — `STDOUT` (texto legível), `STDOUT_JSON` (JSON com campos
`timestamp`, `level`, `logger`, `message`, `service`, `correlationId`, `userId`, `action`,
`entityType`, `entityId`, `durationMs`, `operation`, `errorCode`, `traceId`, `spanId` via MDC),
`LOGSTASH` (TCP, `LogstashEncoder`). Perfis `local`/`test`: só `STDOUT` texto. `dev`: `STDOUT`
texto + habilita log do `CorrelationIdFilter`. `prod`: `STDOUT_JSON` + `LOGSTASH`.

### 8.3 Métricas (Prometheus, via `MetricsPort`/`MetricsAdapter` → `lib-common/MetricsService`)

Exposto em `/actuator/prometheus` (`management.endpoints.web.exposure.include: health,info,prometheus`).
Contadores de negócio emitidos pelos services: `provider_created_total`, `provider_updated_total`,
`provider_deleted_total`, `template_created_total`, `template_updated_total`,
`template_deleted_total`, `message_sent_total` (tags `provider_id`, `type`, `status`),
`email.send.success`/`email.send.failure` (tags `provider`, `service`, via
`DynamicEmailSenderAdapter`). Padrão por-endpoint via `@MetricsEndpoint` (20 ocorrências, lista
completa em `03-libs-lib-go-common.md` seção 3.2): `api_requests_total`/
`api_requests_latency_seconds` com labels `endpoint`/`application`/`status`.

### 8.4 Segurança / auth

**Nenhuma.** Confirmado: sem dependência `lib-security` no pom, sem import de
`com.keepguard.lib_security.*`, sem `Authorization`/`Bearer`/`JWT` em nenhum controller ou
config. Tenant é resolvido só pelo header `X-Company-Id` (REST) ou campo `companyId`/
`X-Tenant-Id` no payload/MDC (consumer RabbitMQ) — sem verificação de assinatura/issuer/
audience. O Helm (`helm/templates/deployment.yaml:32-36`) injeta `JWT_SECRET` como env var
vinda de secret, mas **nenhum código do serviço lê essa variável** — resquício do template
compartilhado de deployment (ver seção 10).

### 8.5 Profiles Spring

| Profile | Porta | Postgres | Redis | RabbitMQ | ddl-auto | Logs |
|---|---|---|---|---|---|---|
| `local` (default, `pom.xml:266-270`) | 8582 | `localhost:5432` | standalone `localhost:6379` | `localhost:5672` | `update` | texto, console |
| `dev` | 8082 | `postgres:5432` | cluster 6 nós | `localhost:5672` | `update` | texto, console |
| `prod` | 8082 | `postgres:5432` | cluster 6 nós | `rabbit-1:5672` | `validate` | JSON + Logstash |
| `test` | aleatória (`server.port:0`) | H2 em memória | `localhost:6379` (não usado de fato nos testes unitários) | — | `create-drop` | WARN |

**Confirmado em `02-consumidores-infra.md`:** o profile efetivamente ativo no Deployment K8s de
produção é `local` (via configmap `keepguard-config`), não `prod` — único serviço Java do
monorepo nessa situação. Consequência prática: filas/exchanges com sufixo `.dev`/`-dev` ativos
em produção, `ddl-auto=update` em vez de `validate`.

---

## 9. Testes Java existentes

46 arquivos em `src/test/java`, cobertura ampla por classe (resumo — não reproduz código):

- `MsCommunicationApplicationTest` — smoke tests de bootstrap (anotações, modificadores, main).
- `MessageSendRabbitMQConsumerTest` (12 testes) — ack em sucesso, nack sem requeue em
  `IllegalArgumentException`, nack com requeue + relança em exceção genérica, fallback crítico,
  IOException ao nack, mensagens com campos nulos/diferentes tipos.
- `MessageSendRabbitMQDTOTest` / `MessageSendResultRabbitMQDTOTest` — validação de `isValid()`,
  `toLogString()`, `isSuccess()`/`hasError()` em todas as combinações.
- `MessageRabbitMQMapperTest` — conversão RabbitMQ DTO → Command, exceção em DTO inválido.
- `HealthControllerTest` — DB saudável/não saudável/exceção, para `healthCheck()` e `health()`.
- `MessageControllerTest` — envio sucesso/falha (sempre 200), DTOs.
- `MessageAdapterMapperTest` — mapeamento request→command, campos nulos, todos os
  `communicationType`.
- `ProviderControllerTest` (30+ testes) — CRUD completo, ativação/desativação/default, listagem
  de tipos, exceções propagando como `RuntimeException` quando `getById` retorna vazio (confirma
  bug da seção 10).
- `TemplateControllerTest` — idem para Template, incluindo teste explícito do endpoint
  `/by-type` usando `templatePort.getById(any(UUID.class))` (confirma bug da seção 10 — o próprio
  teste já reflete que o endpoint busca por UUID arbitrário, não pelos params).
- `N8nWebhookFeignAdapterTest` — envio email/SMS/WhatsApp/push/test, propagação de falha do Feign.
- `SearchCriteriaTest` — DTO de paginação, equals/hashCode/toString, valores extremos.
- `ProviderApplicationMapperTest` / `TemplateApplicationMapperTest` — mapeamentos DTO↔domínio,
  campos opcionais nulos preservando defaults.
- `ProviderQueryServiceTest` / `TemplateQueryServiceTest` — busca por ID (sucesso/NotFoundException),
  search com critérios, listagens, exists.
- `ProviderUseCaseServiceTest` / `TemplateUseCaseServiceTest` — delegação para command/query
  services, conversão de exceção em `Optional.empty()` (confirma padrão da seção 10), tratamento
  de `null` em cada método.
- `*ExceptionTest` (8 classes) — construtores, herança de `RuntimeException`, mensagens
  nulas/vazias/especiais.
- `MessageCommandServiceTest` — `NotFoundException` quando provider/providers ausentes.
- `MessageRabbitMQProcessorServiceTest` (7 testes) — **confirma que o processor nunca propaga
  falha e nunca interage com `EventPublisherPort`** em nenhum cenário (sucesso, falha,
  `IllegalArgumentException`, exceção genérica, campos nulos, diferentes tipos).
- `ProviderTest` / `TemplateTest` — invariantes de domínio (activate/deactivate/
  setAsDefault/updatePriority com proteção contra null/negativo), equals por identidade
  (confirma ausência de `equals`/`hashCode` customizados).
- `CorrelationContextTest` — geração/persistência/limpeza de correlation ID no MDC.
- `RabbitMQConsumerConfigTest` / `RabbitMQProducerConfigTest` / `RabbitMQPropertiesTest` —
  criação de factories/exchanges/filas/bindings com valores mockados.
- `ProviderRepositoryAdapterTest` / `TemplateRepositoryAdapterTest` — delegação ao Spring
  Repository, mapeamento, paginação, critérios nulos.
- `ProviderJpaEntityTest` / `TemplateJpaEntityTest` — builder, defaults, `@PrePersist`/
  `@PreUpdate`, equals/hashCode (aqui SIM declarados via Lombok `@Data` na entity JPA, diferente
  do domínio).
- `ProviderJpaMapperTest` / `TemplateJpaMapperTest` — conversões domínio↔JPA em todas as
  combinações de enum/estado.
- `N8NCommunicationProviderTest` — envio por tipo de comunicação (incluindo fallback Telegram→WhatsApp),
  exceções em falha HTTP/4xx, suporte/não-suporte por tipo de provider.
- `SendGridCommunicationProviderTest` — confirma que a implementação é **placeholder sempre
  `true`** (nenhum teste consegue forçar falha real, pois não há chamada HTTP de fato).
- `PayloadFactoryTest` — criação dos 4 payloads N8N com valores nulos/vazios/diversos.
- `ProviderStrategyFactoryTest` — resolução de estratégia por tipo, pattern matching, provider
  nulo (`NullPointerException` em `supports(null)`).
- `GlobalExceptionHandlerTest` (15 testes) — cada handler, confirmando status/title/type/
  errorCode/propriedades extras (`path`, `timestamp`, `errors`, `operation`, `context`) para
  todas as 11+ exceções mapeadas.
- `ProviderTestBuilder` / `TemplateTestBuilder` (helpers, não testes) — builders fluentes para
  domínio/DTOs de teste; note `configuration.toString()` usado para montar o JSON de
  configuração nos testes (gera formato de `Map.toString()`, não JSON real — ex.
  `{apiKey=test-key}`, não `{"apiKey":"test-key"}` — mas isso é só artefato de teste, não
  comportamento de produção).

---

## 10. Bugs e comportamentos estranhos (comportamento ATUAL — não corrigir)

1. **Swagger exemplo com `templateType` inválido.** `adapters/in/rest/message/MessageController.java:49`
   — `@Schema(example=...)` do endpoint `POST /api/v1/messages/send` usa `"templateType": "WELCOME"`,
   valor que não existe em nenhum dos 16 valores de `TemplateTypeEnum`. Um consumidor que copiar
   o exemplo do Swagger recebe erro de deserialização.

2. **`GET /api/v1/templates/by-type` ignora os query params e busca por UUID aleatório.**
   `adapters/in/rest/template/TemplateController.java:188` —
   `templatePort.getById(UUID.randomUUID()).orElseThrow(...)` descarta completamente `type` e
   `messageType` recebidos. Na prática o endpoint quase sempre retorna 500 (`RuntimeException`
   genérica, ver item 3) e nunca retorna o template correto. Confirmado pelo próprio teste
   `TemplateControllerTest.shouldGetTemplateByTypeSuccessfully` (linha 332), que mocka
   `getById(any(UUID.class))` em vez de validar que os parâmetros `type`/`messageType` foram
   usados.

3. **`RuntimeException` genérica em vez de `NotFoundException` em 3 lookups por ID.**
   `ProviderController.java:133` (`getProviderById`) e
   `TemplateController.java:158,188` (`getTemplateById`, `getTemplateByType`) usam
   `.orElseThrow(() -> new RuntimeException("..."))`. Essa exceção não está mapeada no
   `GlobalExceptionHandler` (que só mapeia tipos específicos + genérico), então cai no handler
   `Exception.class` → sempre **500** ("Erro interno do servidor"), nunca o 404 que a intenção do
   código e a documentação Swagger (`@ApiResponse(responseCode = "404", ...)`) prometem.

4. **`ProviderController.getDefaultProvider` usa `NotFoundException` corretamente, mas com
   `errorCode` inconsistente entre camadas.** `ProviderController.java:251-256` lança
   `NotFoundException` com `errorCode="DEFAULT_PROVIDER_NOT_FOUND"` quando o provider padrão não
   existe — mas `MessageCommandService.sendWithFallback` (linha 92), ao não achar providers
   ativos, usa o construtor de 1 argumento de `NotFoundException`, que fixa `errorCode="NOT_FOUND"`
   (genérico) em vez de algo mais específico como `"PROVIDER_NOT_FOUND"` (usado em
   `sendWithProvider`, linha 48). Três pontos de "provider não encontrado" no código têm 3
   `errorCode`s diferentes (`DEFAULT_PROVIDER_NOT_FOUND`, `NOT_FOUND`, `PROVIDER_NOT_FOUND`).

5. **Cache Redis de Provider/Template (`ProviderCacheService`/`TemplateCacheService`) é código
   morto.** Confirmado por grep: nenhum service de application (`ProviderCommandService`,
   `ProviderQueryService`, `TemplateCommandService`, `TemplateQueryService`,
   `TemplateProcessorService`) injeta `ProviderCachePort`/`TemplateCachePort`. Toda a infra (TTL
   de 7 dias, chaves, circuit breaker, fallback) existe e funcionaria se chamada, mas nunca é.
   O `system-design.md` (seção 2) descreve esse cache como se estivesse ativo — não está.

6. **Eventos de domínio (`MessageSentEvent`/`MessageFailedEvent`) nunca são publicados.**
   `EventPublisherPort`/`RabbitMQEventPublisherAdapter` existem com circuit breaker, retry e
   fallback completos, mas nenhum service (`MessageCommandService`, `MessageRabbitMQProcessorService`)
   os injeta ou chama. Confirmado por `MessageRabbitMQProcessorServiceTest` —
   `verifyNoInteractions(eventPublisherPort)` em todos os 7 cenários de teste. As filas
   `message.sent`/`message.failed` nunca recebem mensagem de fato.

7. **`MessageRabbitMQProcessorService.processMessageSend` nunca propaga falha de envio.**
   `application/service/message/MessageRabbitMQProcessorService.java:28-52` — o retorno
   `boolean` de `messagePort.sendWithFallback(command)` é descartado (linha 37); tanto
   `IllegalArgumentException` quanto `Exception` genérica são capturadas e só logadas, sem
   relançar. Consequência: `MessageSendRabbitMQConsumer` sempre recebe sucesso desta chamada e
   sempre faz `channel.basicAck` (`MessageSendRabbitMQConsumer.java:54`) — uma mensagem cujo
   envio de fato falhou (nenhum provider disponível) é tratada como processada com sucesso pelo
   RabbitMQ: nunca vai para retry, nunca vai para DLQ, e ninguém é notificado da falha (reforça
   o item 6 — nem mesmo o `MessageFailedEvent`, que existiria para isso, é publicado).

8. **`ResilienceConfig` usa `ofDefaults()` e ignora toda a configuração nomeada do YAML.**
   `infrastructure/config/resilience/ResilienceConfig.java:26-69` declara os 5 registries do
   Resilience4j manualmente via `XxxRegistry.ofDefaults()`, suprimindo a auto-configuração do
   Spring Boot (`resilience4j-spring-boot3`, `pom.xml:119-123`) que leria
   `resilience4j.circuitbreaker.configs`/`.instances` (incluindo a instância `n8nClient`,
   `application.yml:160-162,179-185,209-211,219-220`) do `application.yml:141-220`. Em runtime,
   `@CircuitBreaker(name="n8nClient")`/`@Retry(name="n8nClient")` (únicas anotações de
   resiliência de fato presentes no código, em `N8nWebhookFeignAdapter.java:25-54`) criam a
   instância com os puros defaults da biblioteca (sliding window 100 em vez de 10, retry sem
   backoff exponencial em vez de `x2`), não com os valores customizados do YAML. Mesmo padrão de
   bug já catalogado no `ms-auth` (citado no prompt original desta tarefa). Além disso, o
   bulkhead de `n8nClient` (25 chamadas concorrentes, configurado no YAML) nunca teria efeito de
   qualquer forma, porque **não existe nenhuma anotação `@Bulkhead`** em
   `N8nWebhookFeignAdapter.java`.

9. **`getProviderById`/`getDefaultByCommunicationType` em `ProviderUseCaseService` engolem
   qualquer exceção, não só "não encontrado".** `application/service/provider/ProviderUseCaseService.java:37-44,63-69`
   — `try { ... } catch (Exception e) { return Optional.empty(); }` captura qualquer `Exception`,
   não só `NotFoundException`. Um erro real de infraestrutura (banco fora, erro de mapeamento)
   fica indistinguível de "recurso não encontrado" para quem chama — e como o controller
   (`ProviderController.getProviderById`, linha 133) lança `RuntimeException("Provider not
   found")` genérica ao receber `Optional.empty()`, o cliente final recebe sempre 500 de
   qualquer forma (ver item 3), mas com a causa raiz do erro real perdida nos logs.

10. **`getDefaultByCommunicationType` em `ProviderUseCaseService` tem um `NullPointerException`
    mascarado.** `application/service/provider/ProviderUseCaseService.java:64-69`:
    ```java
    ProviderViewDTO view = queryService.getDefaultByCommunicationType(communicationType).orElse(null);
    return Optional.of(view);
    ```
    Quando não existe provider padrão, `view` é `null`, e `Optional.of(null)` **lança
    `NullPointerException`** (API do `Optional` exige `ofNullable` para aceitar `null`). Essa
    NPE é capturada pelo `catch (Exception e)` da própria linha 63-69 (o bloco try/catch
    completo) e convertida em `Optional.empty()` — o resultado final observável é o mesmo que o
    caso "não encontrado" deveria produzir, mas por acidente de implementação (NPE mascarado),
    não por lógica correta. Qualquer refatoração que remova esse catch quebra silenciosamente.

11. **`setAsDefault` não desmarca outros providers do mesmo tipo.**
    `application/service/provider/ProviderCommandService.java:197-215` — `setAsDefault(UUID id)`
    só chama `provider.setAsDefault()` no provider alvo e salva; nunca busca/desmarca outros
    providers com `isDefault=true` do mesmo `communicationType`. Múltiplos providers podem ficar
    marcados como default simultaneamente para o mesmo tipo de comunicação — a query
    `findDefaultProviderByCommunicationType` (`ProviderSpringRepository.java:25-26`) não tem
    `LIMIT 1`/`findFirst`, então o comportamento com múltiplos defaults depende da ordem de
    retorno do banco (indefinida sem `ORDER BY`).

12. **Delete de provider não bloqueia providers ativos, apesar do Swagger documentar que deveria.**
    `ProviderController.java:270` documenta `@ApiResponse(responseCode="400", description="Provedor
    ativo não pode ser deletado")`, mas `ProviderCommandService.delete`
    (`application/service/provider/ProviderCommandService.java:142-155`) só checa existência —
    nunca verifica `isActive`. Deletar um provider ativo funciona normalmente (204), sem erro.

13. **Validação `@NotBlank`/`@NotNull` inoperante em campos `UUID`.**
    `application/dto/message/MessageSendCommandDTO.java:20` (`@NotBlank private UUID companyId`)
    e `application/dto/provider/ProviderCreateCommandDTO.java:20` (`@NotNull private UUID
    companyId` — este usa `@NotNull`, correto; mas o primeiro usa `@NotBlank` num campo `UUID`,
    não `String`) — `@NotBlank` do Bean Validation só tem efeito em `CharSequence`; aplicado a um
    campo `UUID`, a anotação é simplesmente ignorada em runtime (não há erro de compilação, mas
    também nunca dispara). Como esses DTOs de `*CommandDTO` não passam por `@Valid` em nenhum
    controller (são montados manualmente pelos mappers, não recebidos direto como `@RequestBody`),
    essa validação nunca executa de qualquer forma — é "morta" por duplo motivo.

14. **`TemplateApplicationMapper.toCreateCommand`/`toUpdateCommand` e
    `ProviderApplicationMapper.toCreateCommand`/`toUpdateCommand` são no-ops.**
    `application/mapper/TemplateApplicationMapper.java:15-27` e
    `application/mapper/ProviderApplicationMapper.java:15-41` — cada método recebe um DTO e
    simplesmente `return dto;` (mesmo tipo de entrada e saída), com um comentário explícito "Apenas
    retorna o mesmo DTO já que são do mesmo tipo". Código morto/vestigial de uma camada de mapeamento
    que nunca fez sentido ali (provavelmente resquício de um refactor anterior).

15. **Inconsistência de nome de campo JSON (`companyId` vs `application`) entre Response DTOs de
    Template.** `TemplateCreateResponseDTO`/`TemplateGetTemplatesResponseDTO` serializam o campo
    como `"companyId"` (`@JsonProperty("companyId")`, explícito nas linhas 35-36 e 35-36
    respectivamente); `TemplateGetTemplateByIdResponseDTO`/`TemplateGetTemplateByTypeResponseDTO`/
    `TemplateUpdateResponseDTO` usam o nome de campo Java `application` sem `@JsonProperty`, então
    serializam como `"application"`. Um cliente que espera `companyId` em todos os endpoints de
    Template quebra ao consumir `GET /{id}`, `GET /by-type` ou `PUT /{id}`.

16. **`SendGridCommunicationProvider` é um placeholder que sempre retorna sucesso fictício.**
    `infrastructure/provider/communication/SendGridCommunicationProvider.java:22-41,48-66` — tanto
    `sendMessage` quanto `testConnection` têm comentários `"// Implementação específica do
    SendGrid"` / `"// Placeholder para demonstração"` e sempre retornam `true`, sem nenhuma
    chamada HTTP real ao SendGrid. Qualquer provider cadastrado com `providerType=SENDGRID`
    "funciona" sempre, mesmo que a configuração/API key esteja completamente errada — nenhuma
    mensagem é de fato enviada. Confirmado por `SendGridCommunicationProviderTest` (nenhum teste
    consegue forçar uma falha real).

17. **Método de repositório duplicado e nunca usado em `TemplateSpringRepository`.**
    `infrastructure/persistence/spring/TemplateSpringRepository.java:46-56`
    (`findByTemplateTypeAndMessageTypeAndTenantIdAndIsActive`) é idêntico em query JPQL a
    `findTemplateByTypeMessageAndApp` (linhas 58-69), que é o único de fato chamado pelo
    `TemplateRepositoryAdapter`. Confirmado por grep: o primeiro método nunca aparece em nenhuma
    chamada em `src/main` ou `src/test`.

18. **`JWT_SECRET` injetado no Deployment Helm sem nenhum uso no código.**
    `helm/templates/deployment.yaml:32-36` define a env var `JWT_SECRET` a partir do secret
    `keepguard-secret`, mas (confirmado em toda a leitura do serviço, seção 8.4) não existe
    `lib-security`, JWT, nem qualquer leitura de `JWT_SECRET`/variável equivalente no código Java
    do serviço. É resquício do template de deployment compartilhado do monorepo.

19. **Endpoint de debug sem proteção em produção.** `TemplateController.java:223-230`
    (`GET /api/v1/templates/teste-hot-reload`) é um endpoint de teste de hot-reload, sem
    `@MetricsEndpoint`, sem autenticação (nenhum endpoint tem, mas este nem tenta documentar
    propósito de negócio), retornando só uma string com timestamp. Comentários no código
    (`"🔥 HOT RELOAD TESTE FINAL"`, linha 35) confirmam que é resquício de debug, não
    funcionalidade de produto. Não deve ser portado para o serviço Go.

20. **`ProviderTestProviderConnectionResponseDTO` é código órfão.**
    `adapters/in/rest/provider/dto/response/ProviderTestProviderConnectionResponseDTO.java`
    existe, e `ProviderAdapterMapper.toTestProviderConnectionResponseDTO`
    (`adapters/in/rest/provider/mapper/ProviderAdapterMapper.java:375-387`) sabe construí-lo, mas
    nenhum endpoint em `ProviderController` o expõe. Infraestrutura de "testar conexão do
    provider" (que existe de fato em `CommunicationProvider.testConnection`,
    `N8NCommunicationProvider`, `GoogleEmailSenderCommunicationProvider`,
    `SmsSenderCommunicationProvider`, `SendGridCommunicationProvider`) nunca é exposta via REST.

21. **`PUT` de Provider/Template sobrescreve campos com `null` em update parcial.**
    `ProviderCommandService.update` (linhas 111-123) seta todos os campos do comando
    diretamente no domínio existente, sem checagem de `null` (diferente de `create`, que condiciona
    cada campo opcional). Um client que envie um JSON de `PUT` omitindo um campo opcional (que
    desserializa como `null` no DTO) **apaga** o valor anteriormente salvo nesse campo, em vez
    de preservá-lo — comportamento de "replace total", não de "merge/patch", apesar do método
    HTTP ser `PUT` (que semanticamente já é replace, mas o Swagger do endpoint não avisa essa
    consequência e nenhum campo do `ProviderUpdateRequestDTO` é obrigatório exceto
    `name`/`providerType`/`communicationType`).

22. **Formato de timestamp inconsistente entre `HealthController`, DTOs normais e
    `GlobalExceptionHandler`.** `HealthController.healthCheck()` (linha 75) serializa
    `timestamp` via `LocalDateTime.now().toString()` (formato Java padrão, inclui nanossegundos
    variáveis, ex. `2024-01-15T10:30:00.123456`); os demais DTOs (Provider/Template) usam o
    `ObjectMapper` de `JacksonConfig` com padrão fixo `yyyy-MM-dd'T'HH:mm:ss` (sem fração de
    segundo); `GlobalExceptionHandler` usa `DateTimeFormatter.ISO_LOCAL_DATE_TIME` (inclui
    fração de segundo variável quando não-zero, omite quando zero). Três formatos de data/hora
    diferentes coexistem na mesma API.

23. **`ObjectMapper` customizado de `JacksonConfig` pode não ser o `ObjectMapper` ativo do Spring
    MVC.** `infrastructure/config/JacksonConfig.java:16-31` declara um bean `ObjectMapper`
    customizado (enum deserialization via `toString()`, `LocalDateTime` sem milissegundos). Como
    é um `@Bean` simples sem `@Primary` explícito e sem substituir o
    `Jackson2ObjectMapperBuilder` que o Spring Boot normalmente usa para montar o `ObjectMapper`
    do `MappingJackson2HttpMessageConverter`, **não há garantia, só pela leitura deste arquivo,
    de que é de fato esse bean que serializa as respostas REST** — pode coexistir com o
    `ObjectMapper` autoconfigurado padrão do Spring Boot, e qual dos dois efetivamente serializa
    cada resposta depende de como o Spring resolve a ambiguidade de beans (normalmente o
    `@Bean` do usuário prevalece se for do mesmo tipo exato, mas isso não foi confirmado em
    runtime nesta leitura só de código-fonte). Vale validar em ambiente real antes de assumir
    que o formato `yyyy-MM-dd'T'HH:mm:ss` é garantidamente o que chega ao cliente em todas as
    respostas.

24. **CHECK constraint de `template_type` em produção está incompleto — falta `FATURA_PAGA` e
    `FATURA_VENCIDA`.** Confirmado via `pg_dump -s` real de prod
    (`docs/migracao-go/ms_communication_schema_prod.sql:79`): a constraint
    `templates_template_type_check` da tabela `ms_communication.templates` em produção só lista
    **14** dos 16 valores de `TemplateTypeEnum` — `FATURA_PAGA` e `FATURA_VENCIDA` (os dois
    valores usados pelo fluxo de billing, item 4.7/consumer `BillingInvoiceFactRabbitMQConsumer`)
    não estão na lista. Como `ddl-auto` efetivo em produção é `update` (não `validate`, ver item
    4 de `02-consumidores-infra.md`), a constraint nunca foi recriada para incluir esses 2
    valores depois que o enum Java cresceu — Hibernate `update` não altera CHECK constraints
    existentes em colunas já existentes. **Consequência prática**: tentar criar (`POST
    /templates`) um `Template` com `templateType=FATURA_PAGA` ou `FATURA_VENCIDA` falha em
    produção com violação de CHECK constraint (500 via handler genérico, não um erro de negócio
    mapeado) — apesar do enum Java aceitar esses valores e o Swagger documentá-los como válidos.
    Relevante para a migration Go: a baseline SQL (`db/migrations/001_..._baseline.up.sql`)
    precisa decidir se replica o CHECK incompleto (paridade estrita com o bug) ou já nasce
    completo com os 16 valores (ver D6 do `README.md` — recomendação é completar, por ser
    `ALTER TABLE ... ADD CONSTRAINT` aditivo, não uma mudança de comportamento observável de API,
    e por desbloquear um fluxo de billing hoje quebrado).

---

## Resumo rápido para quem for programar o serviço Go (não é seção do prompt — índice de navegação)

- Enums de comunicação: usar `lib-go-common/comm` direto, sem alterações (seção 3.3).
- `ProviderTypeEnum`: criar tipo local no serviço Go (seção 3.3, 4 valores).
- Endpoint `/by-type` de template: decidir explicitamente se a migração implementa a busca real
  (que nunca existiu em produção) ou replica o bug atual — decisão de produto, não técnica
  (item 2).
- Cache Redis de Provider/Template: decidir se liga de verdade na migração ou se mantém morto
  como hoje (item 5) — se ligar, a chave/TTL/formato já estão especificados na seção 6.
- Eventos de domínio (`message.sent`/`message.failed`): mesma decisão — nunca publicados hoje
  (item 6); migrar a infra sem o uso real não agrega nada à regressão.
- Resiliência dos 3 Feign clients: usar o padrão decorator do bff-auth, não replicar o bug do
  `ofDefaults()` (seção 7.2, item 8, e `04-resiliencia-integracoes.md` seção 5).
- Fila `keepguard.notifications.sms`: contrato externo real, preservar nome/formato exatos
  (seção 7.3, `02-consumidores-infra.md` item crítico).
- Perfis/nomes de fila ativos em produção hoje são os de `.dev` (seção 8.5,
  `02-consumidores-infra.md` seção 4) — decidir antes do cutover se a migração corrige isso.
