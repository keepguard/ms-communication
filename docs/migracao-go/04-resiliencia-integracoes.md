# 04 — Resiliência em integrações de saída e consumers (ms-communication → Go)

Levantamento de estado real do código Java (ms-communication), sem escrita de código Go.
Toda referência é `arquivo:linha` do estado atual do repositório.

## 1. Clients Feign — timeout, retry, circuit breaker, bulkhead

### 1.1 BillingUserClient

- Interface: `adapters/out/feign/BillingUserClient.java:11-23`. `GET /internal/v1/users/{id}`,
  `url = ${USER_SERVICE_URL:http://localhost:8085}`.
- Config: `adapters/out/feign/BillingUserClientConfig.java:8-14` — **só define o nível de log**
  (`Logger.Level.BASIC`). Não há `Request.Options` (timeout fica no default do Feign,
  normalmente herdado do `feign.client.config.default` do Spring Cloud OpenFeign, que **não
  está declarado em nenhum YAML deste serviço** — logo, timeout efetivo é o default da lib
  Feign/`httpclient`, não um valor pensado para este client).
- Retry: nenhum (nem anotação `@Retry`, nem retryer Feign customizado).
- Circuit breaker: nenhum (nenhuma anotação `@CircuitBreaker`, nenhuma instância
  `billingUserClient` nem similar em `application.yml`).
- Bulkhead: nenhum.
- Uso real: chamado em `adapters/in/messaging/rabbitmq/consumer/BillingInvoiceFactRabbitMQConsumer.java:59`
  dentro de um consumer RabbitMQ, sem nenhuma proteção. Falha é tratada só por
  `catch (RuntimeException ex)` no próprio consumer (linha 61-65), que desfaz a chave de
  idempotência no Redis e retorna sem relançar — ou seja, a mensagem recebe ack implícito
  do container Spring (o listener não lança), **não há retry nem requeue** se o
  billing-user-service estiver fora.
- Operação é idempotente? Sim — `GET` de leitura pura, sem efeito colateral. Retry é seguro.

### 1.2 DynamicEmailSenderClient

- Interface: `adapters/out/feign/DynamicEmailSenderClient.java:23-34`. `POST`/`GET` com URL
  dinâmica por provedor de e-mail (`URI` passado por parâmetro sobrescreve o `url` do
  `@FeignClient`, que é só um placeholder `http://email-placeholder.local`).
- Timeout: **o único client dos 3 com timeout explícito** —
  `DynamicEmailSenderClientConfig.java:17-19`: `Request.Options(connect=10s, read=30s, followRedirects=true)`.
  Esse timeout é aplicado via bean `Request.Options`, mecanismo nativo do Feign — chega ao
  client em runtime independente do Resilience4j.
- Retry: nenhuma anotação `@Retry`. Existe um bloco `http.client.retry.*` em
  `application.yml:72-75` (`max-attempts: 3`, `initial-interval: 1000`, `max-interval: 3000`),
  mas é um `@ConfigurationProperties` sob o prefixo `http.client`, não `resilience4j.retry` —
  **não tem nenhuma instância Resilience4j nem retryer Feign amarrados a ele**; é
  provavelmente lido por algum outro componente HTTP genérico, não pelo
  `DynamicEmailSenderClient`. Não encontrei nenhum uso de `http.client.retry` no código-fonte
  deste client.
- Circuit breaker: nenhum.
- Bulkhead: nenhum.
- Operação é idempotente? Depende: `sendMail` (POST) **não é idempotente** por padrão (reenviar
  pode duplicar o e-mail no provedor) — a idempotência de fato é feita a montante, por chave
  Redis no consumer de billing (`BillingInvoiceFactRabbitMQConsumer.java:50-55`), não no client.
  `health` (GET) é idempotente e pode ter retry livre.

### 1.3 N8nWebhookClient

- Interface: `adapters/out/feign/N8nWebhookClient.java:20-24`. `POST` com URL dinâmica por
  tenant (mesmo padrão do email sender).
- Timeout: `N8nWebhookClientConfig.java:17-19` — mesmo `Request.Options(10s/30s)` do email
  sender, via Feign nativo.
- Retry e circuit breaker: **é o único client com anotações Resilience4j de fato presentes no
  código** — `N8nWebhookFeignAdapter.java:25-26, 32-33, 39-40, 46-47, 53-54`:
  `@CircuitBreaker(name = "n8nClient")` + `@Retry(name = "n8nClient")` em todos os 5 métodos
  (`sendEmail`, `sendSMS`, `sendWhatsApp`, `sendPush`, `testConnection`). O YAML tem config
  dedicada para `n8nClient` em circuitbreaker (`application.yml:160-162`), retry
  (`application.yml:179-185`), bulkhead (`application.yml:209-211`) e timelimiter
  (`application.yml:219-220`) — é o client mais bem especificado no papel: retry 3x/500ms,
  backoff x2, circuit breaker com `baseConfig: default` (janela 10, threshold 50%, 60s aberto),
  bulkhead 25 chamadas concorrentes, timeout 15s.
- **Bug confirmado (ver seção abaixo): essa config do YAML não chega às anotações em runtime.**
- Bulkhead: declarado no YAML (`n8nClient`, 25 concorrentes) mas **sem anotação
  `@Bulkhead` em nenhum método do `N8nWebhookFeignAdapter`** — mesmo se o registry
  funcionasse, o bulkhead não seria aplicado por falta da anotação.
- Operação é idempotente? Não, em geral — é uma chamada de webhook de envio (e-mail/SMS/
  WhatsApp/push) via N8N. Retry de uma operação não idempotente arrisca duplicar notificação
  ao usuário final. `testConnection` é seguro para retry (não tem efeito observável pelo
  usuário).

### 1.4 Confirmação do bug — registries `ofDefaults()` ignoram o YAML

`infrastructure/config/resilience/ResilienceConfig.java:26-69` declara **manualmente** os 5
beans (`CircuitBreakerRegistry`, `RetryRegistry`, `BulkheadRegistry`, `RateLimiterRegistry`,
`TimeLimiterRegistry`), todos via `XxxRegistry.ofDefaults()`, e só os amarra ao `MeterRegistry`
para métricas (`TaggedXxxMetrics...bindTo(meterRegistry)`).

Isso **repete exatamente o bug já encontrado no ms-auth**: o módulo
`resilience4j-spring-boot3` (presente no `pom.xml:120-121`) tem uma auto-configuração
(`Resilience4jAutoConfiguration`) que é quem normalmente lê `resilience4j.circuitbreaker.configs`,
`resilience4j.retry.configs` etc. do `application.yml` e popula os registries com essas
configurações nomeadas (`default`, `n8nClient`, ...). Essa auto-configuração só atua quando
**não existe** um bean do tipo `XxxRegistry` já declarado pelo usuário
(`@ConditionalOnMissingBean`). Como `ResilienceConfig.java` declara os 5 beans manualmente —
e com `ofDefaults()`, sem ler nenhuma property —, a auto-configuração nativa é suprimida e os
blocos `resilience4j.*` do `application.yml:141-220` (incluindo toda a config nomeada de
`n8nClient`) **nunca chegam aos registries**.

Consequência prática: quando `@CircuitBreaker(name = "n8nClient")` e `@Retry(name = "n8nClient")`
pedem a instância `"n8nClient"` ao registry, ela não existe pré-configurada — o
Resilience4j cria essa instância on-the-fly usando os valores puramente default da biblioteca
(circuit breaker: sliding window 100, threshold 50%, wait 60s; retry: 3 tentativas, wait 500ms
fixo sem backoff), e **não** os valores customizados do YAML (sliding window 10, backoff x2,
bulkhead 25, timeout 15s etc.). O efeito observável é sutil — os nomes dos parâmetros-default
do Resilience4j por acaso são próximos dos do YAML em alguns campos (ex.: retry 3 tentativas é
default da lib E também o valor do YAML para `n8nClient`), o que mascara o bug em teste manual
superficial; mas o `exponentialBackoffMultiplier: 2`, o bulkhead e o timelimiter de 15s do
`n8nClient` certamente **não** são aplicados, porque não há nenhuma anotação `@Bulkhead`/
`@TimeLimiter` sequer no código, e o retry default da lib não tem backoff exponencial igual
ao configurado.

**Confirmado: o bug do ms-auth se repete no ms-communication.** Mesmo padrão: registries
manuais com `ofDefaults()` suprimindo a auto-configuração do Spring Boot que leria o YAML.

### 1.5 Padrão Go equivalente recomendado por client

| Client | Retry (se idempotente) | Circuit breaker | Observação |
|---|---|---|---|
| BillingUserClient | Sim — GET puro, idempotente. 2-3 tentativas, backoff curto. | Sim — é dependência síncrona dentro de um consumer RabbitMQ; se cair, pode travar o fluxo de e-mail de fatura. CB evita bater repetidamente num serviço fora. | Hoje sem nenhuma proteção; é a maior lacuna real dos 3. |
| DynamicEmailSenderClient | Só no `health` (idempotente). No `sendMail`, retry sem dedupe arrisca e-mail duplicado — manter a idempotência que já existe hoje via chave Redis no consumer, não adicionar retry "ingênuo" dentro do client. | Sim — provedor de e-mail externo instável é candidato típico a cascata. | Timeout 10s/30s já existe e deve ser preservado. |
| N8nWebhookClient | Só em `testConnection`. Nos sends (e-mail/SMS/WhatsApp/push), mesmo cuidado do email sender — não duplicar notificação ao usuário por retry automático. | Sim, claramente — é o único cliente com CB+retry+bulkhead+timelimiter *desenhados* no YAML hoje (ainda que não aplicados), confirma que a intenção arquitetural já era tratá-lo como ponto de falha preocupante. | Bulkhead (limitar concorrência) faz sentido migrar também, dado que o YAML já previa 25 concorrentes. |

## 2. Consumers RabbitMQ

### 2.1 MessageSendRabbitMQConsumer (`adapters/in/messaging/rabbitmq/consumer/MessageSendRabbitMQConsumer.java`)

- Fila: `${rabbitmq.queues.message-send}` → `ms.communication.message.send.dev` (dev).
- Idempotência: **não há chave de dedupe** no consumer em si (sem Redis `setIfAbsent` nem
  verificação de ID já processado) — linha 30-33. A idempotência, se existir, teria que vir do
  publisher (correlationId é só logado, `rabbitMQMessage.xCorrelationId()`, não usado para
  dedupe).
- Ack: **manual, feito só depois de processar com sucesso** — `channel.basicAck(deliveryTag, false)`
  na linha 54, após `messageSendRabbitMQPort.processMessageSend(...)` retornar sem exceção.
  Confirma que o listener container está em modo manual
  (`spring.rabbitmq.listener.simple.acknowledge-mode: manual`, `application.yml:109`).
- Erro de validação (`IllegalArgumentException`, linha 59-77): `basicNack(deliveryTag, false, false)`
  — nack **sem requeue**, vai direto para a DLQ (porque a fila principal tem
  `x-dead-letter-exchange`/`x-dead-letter-routing-key` configurados —
  `RabbitMQProducerConfig.java:110-113`).
- Outro erro qualquer (linha 78-93): `basicNack(deliveryTag, false, true)` — nack **com
  requeue**, mensagem volta para o fim da fila principal para retentar depois, e a exceção é
  relançada (linha 92), o que também aciona `@Retry(name = "rabbitMQMessageProcessor")` e
  `@CircuitBreaker(name = "rabbitMQMessageProcessor", fallbackMethod = "fallbackProcessMessage")`
  (linhas 28-29) — **mesmo bug de `ofDefaults()` da seção 1.4 afeta esse retry/CB também**,
  já que usa o mesmo `RetryRegistry`/`CircuitBreakerRegistry` globais.
- Fallback crítico (`fallbackProcessMessage`, linha 99-114): quando o Resilience4j esgota
  retries/abre o circuito, o fallback faz `basicNack(deliveryTag, false, false)` — sem
  requeue, direto para DLQ.
- DLQ: sim, configurada — fila principal `messageSendRequestsQueue`
  (`RabbitMQProducerConfig.java:108-114`) aponta para `deadLetterExchange` → `deadLetterQueue`
  = `${rabbitmq.queues.message-send-requests-dlt}` com TTL de 7 dias
  (`x-message-ttl: 604800000`, linha 124-127).

### 2.2 BillingInvoiceFactRabbitMQConsumer (`adapters/in/messaging/rabbitmq/consumer/BillingInvoiceFactRabbitMQConsumer.java`)

- Fila: `${rabbitmq.queues.billing-invoice}` → `ms.communication.billing.invoice.dev`.
- Idempotência: **sim, e é o único dos dois consumers com dedupe real** — chave
  `billing:email:{paid|overdue}:{invoiceId}` no Redis via `setIfAbsent` com TTL configurável
  (`billing.email.idempotency-ttl-hours`, default 168h = 7 dias) — linhas 50-51. Se a chave já
  existe, loga e retorna sem reprocessar (linha 52-55). Em caso de falha depois de reservar a
  chave (erro ao resolver e-mail do pagador, ou `sendWithFallback` retornando `false`, ou
  exceção), a chave é **removida** (`redis.delete(idempotencyKey)`, linhas 63, 98, 102) para
  permitir reentrega/retentativa futura — é um padrão de "reserva otimista com rollback".
- Ack: **não há `Channel`/`deliveryTag` neste consumer** (assinatura do método não recebe
  `Channel`, linha 34) — o ack é implícito, controlado pelo container Spring no modo
  configurado globalmente (`manual` em `application.yml:109`, mas sem acesso explícito ao
  `Channel` aqui, o Spring AMQP faz ack automático ao final do método se não houver exceção,
  **desde que o `SimpleMessageListenerContainer` padrão esteja em uso** — vale confirmar em
  runtime, pois é uma configuração implícita, não explícita como no outro consumer).
- Em erro (linha 100-104): `catch (RuntimeException ex)` relança a exceção (`throw ex`, linha
  103) depois de desfazer a chave de idempotência — isso faz o container nack/requeue (ou
  mandar para DLQ, dependendo da config de `default-requeue-rejected`, que está `false` em
  `application.yml:113`, ou seja, **rejeitado vai para DLQ, não requeue**, se essa fila tiver
  DLX configurado).
- DLQ: sim — `billingInvoiceQueue` em `BillingInvoiceRabbitConfig.java:32-37` tem
  `x-dead-letter-exchange`/`x-dead-letter-routing-key` apontando para o mesmo
  `deadLetterExchange`/fila de falha do outro consumer.
- Nenhuma anotação `@Retry`/`@CircuitBreaker` neste consumer — a chamada a `userClient.getUserById`
  (BillingUserClient) dentro dele roda sem nenhuma proteção (consistente com a seção 1.1).

## 3. Decorator pattern do bff-auth — molde real

Correção de rota: o pacote não é `out/http/resilient` (não existe no repo) — é
`internal/adapters/out/http/decorator/<dominio>/`, um subpacote por client (`auth`, `company`,
`user`, `communication`) mais `internal/infrastructure/resilience/circuit_breaker.go` para o
gerenciador compartilhado de circuit breakers (usa `sony/gobreaker`).

### 3.1 Interface comum

Cada decorator implementa a mesma interface de porta do client que decora (ex.:
`portsclient.CommunicationClient` em `internal/application/port`), que define o(s) método(s)
de negócio (`SendMessage(ctx, req, tenantId, correlationID) (ResponseDTO, error)`,
`GetByTenantId(ctx, tenantId, correlationID) (DTO, error)`). Não há uma interface
genérica `Decorator[T]` — cada decorator é hand-rolled por domínio, repetindo a assinatura do
client que envolve. Cada construtor (`NewRetryDecorator`, `NewCircuitBreakerDecorator`, ...)
recebe o `inner` (a porta) e devolve a própria porta, permitindo encadear.

### 3.2 Decorators disponíveis (por domínio)

- **Retry** (`decorator/<dominio>/retry_decorator.go`): backoff exponencial com jitter
  configurável (`MaxAttempts`, `InitialDelay`, `MaxDelay`, `Multiplier`, `Jitter`), decide se
  é retryable olhando o tipo de erro (`*appdto.HTTPError` com status 429/408/503/504/502 →
  retryable; outros tipos de erro, ex. rede, também retryable por default). Exemplo:
  `decorator/communication/retry_decorator.go:25-33` usa `MaxAttempts: 4, InitialDelay: 200ms,
  MaxDelay: 10s, Multiplier: 2.5, Jitter: true` — mais agressivo que o do `company`/`auth`
  porque é API externa (e-mail) considerada instável.
- **Circuit breaker** (`decorator/<dominio>/circuit_breaker_decorator.go`): delega a um
  `*resilience.CircuitBreakerManager` compartilhado (`internal/infrastructure/resilience/circuit_breaker.go`),
  que por sua vez envolve `sony/gobreaker.CircuitBreaker` por nome de serviço
  (`GetOrCreate(service, config)`). `DefaultIsSuccessful` (linha 48-75) trata 4xx como sucesso
  de negócio (não conta como falha de infra) e só 5xx/erro de rede/timeout como falha real.
- **Logging** (`decorator/<dominio>/logging_decorator.go`): loga início, duração e
  erro/sucesso via `zap.Logger`, sempre envolvendo a chamada ao `inner`.
- **Metrics** (`decorator/<dominio>/metrics_decorator.go`): registra duração e status code
  (extraído de `*appdto.HTTPError`) via `metrics.RecordUpstreamRequest`/`RecordUpstreamError`.
- **Cache Redis** (só em `company` e `user`, não em todos): consulta Redis antes do HTTP; se
  `cache == nil`, o construtor devolve o próprio `inner` sem decorar nada
  (`decorator/company/redis_cache_decorator.go:57-59`) — é opcional por design.

### 3.3 Composição real no wire-up (`cmd/bff-auth/main.go`)

A ordem **não é fixa entre clients** — cada um compõe os decorators que faz sentido para seu
perfil de risco, de dentro para fora (o primeiro a envolver o client HTTP cru é o decorator
mais "interno", executado por último na cadeia de chamada):

- **auth** (linhas 148-180): `Request → Logging → CircuitBreaker → Retry → Metrics → Redis
  user_cache → HTTPClient`. Comentário do próprio código documenta essa ordem.
- **company** (linhas 193-221): `Request → Logging → CircuitBreaker → Retry → RedisCache →
  Metrics → HTTPClient`.
- **communication** (linhas 248-267): `Request → Logging → Retry → Metrics → HTTPClient` —
  **sem circuit breaker** (único dos 4 clients HTTP do bff-auth sem CB), porque o comentário
  do código (linha 269) classifica esse cliente como "retry agressivo (4x) + logging + metrics
  — 90% falhas recuperadas": a decisão de produto foi confiar no retry agressivo em vez de CB
  para esse client especificamente.
- **messaging/RabbitMQ publisher** (linhas 275-294): `Publisher → Logging → Metrics →
  CircuitBreaker (com fallback HTTP)` — um 4º decorator customizado (`messagingDecorator`,
  pacote `adapters/out/messaging/decorator`), fora do padrão HTTP, mas mesmo espírito.

Ordem de construção no código (de dentro para fora) para `communication`, como exemplo
literal:
```
baseCommunicationClient := httpclient.NewCommunicationClient(cfg, zapLogger)
communicationMetricsClient := communicationdecorator.NewCommunicationMetricsDecorator(baseCommunicationClient, metrics, "ms-communication")
communicationRetryClient := communicationdecorator.NewRetryDecorator(communicationMetricsClient, communicationRetryConfig)
communicationClient := communicationdecorator.NewCommunicationLoggingDecorator(communicationRetryClient, zapLogger, "ms-communication")
```

## 4. Alternativa: `ms-analyst-finance/internal/infrastructure/resilience`

Padrão estrutural diferente: em vez de decorators por domínio implementando a porta de
negócio, há um `resilience.Client` genérico (`http_client.go:14-29`) que implementa
`HTTPDoer` (`Do(req) (*http.Response, error)`) e **compõe retry + circuit breaker internamente**
num só tipo (não como decorators empilháveis) — `http_client.go:31-87`: cada tentativa checa o
breaker (`Allow`), faz a chamada, classifica o status (`ClassifyStatus`) e decide retry vs.
`RecordSuccess`/`RecordFailure` no mesmo laço. O circuit breaker (`circuit.go`) é um
`Manager` com state machine própria (Closed/Open/HalfOpen) por `source` (não usa `gobreaker`).//
É mais simples de instanciar (um único `resilience.NewClient(timeout, retry, breaker)` que
implementa a interface padrão de HTTP client), mas menos componível — não dá para adicionar
logging/metrics/cache como camadas independentes sem message "inchar" o `Client` ou herdar de
outro decorator externo a esse pacote.

## 5. Recomendação por client (ms-communication → Go)

- **BillingUserClient → padrão bff-auth (decorator composto), com Retry + CircuitBreaker.**
  É uma dependência síncrona dentro do processamento de um evento de billing; hoje está sem
  nenhuma proteção (pior caso real dos 3). Operação idempotente (GET) — retry seguro. Risco de
  cascata real: se o billing-user-service degradar, o consumer de invoice trava/renasce sem
  controle. Ordem sugerida: `Logging → CircuitBreaker → Retry → Metrics → HTTPClient` (mesmo
  molde do `auth`/`company` do bff-auth, que também são GETs de leitura).
- **DynamicEmailSenderClient → padrão bff-auth, com Retry restrito a `health` e CircuitBreaker
  sempre.** Preservar os timeouts explícitos (10s/30s) que já existem em `Request.Options`.
  Não adicionar retry automático ao `sendMail` sem dedupe — a idempotência deve continuar
  explícita na camada de aplicação (como já é hoje via Redis no consumer de billing), não
  implícita num decorator genérico. Circuit breaker se justifica: provedor de e-mail externo
  é candidato clássico a falha em cascata.
- **N8nWebhookClient → padrão bff-auth, decorator completo (Retry + CircuitBreaker + Bulkhead +
  Logging + Metrics).** É o client que o próprio time já sinalizou como preocupante (único com
  CB+retry+bulkhead+timelimiter desenhados no YAML, ainda que não aplicados hoje). Retry só em
  `testConnection`; nos sends, mesmo cuidado de não duplicar notificação — se for necessário
  retry nesses métodos, exigir idempotência explícita a montante (ex. dedupe por mensagem),
  não assumir que retry automático é seguro. Bulkhead (limite de concorrência, ~25) vale
  migrar de verdade — hoje está no papel (YAML) mas nunca aplicado (nem tem anotação
  `@Bulkhead` no Java atual).

Nenhum dos 3 clients tem perfil que justifique o padrão simplificado do ms-analyst-finance
(que faz sentido quando não se precisa de logging/metrics/cache como camadas independentes).
Como os 3 têm contrato de negócio mais rico que "HTTP genérico" (idempotência condicionada à
operação, diferentes políticas por client) e o monorepo já tem o padrão decorator maduro e
testado no bff-auth, recomenda-se **usar o mesmo pacote `decorator/<client>/` do bff-auth como
molde para os 3**, um subpacote por client Feign migrado.

## Ressalvas sobre as referências usadas

- O consumer Go de LGPD do ms-auth-go (`internal/adapters/in/rabbitmq/usererasure/user_erasure_consumer.go`)
  **não é bom molde de idempotência/DLQ** — faz sempre `Ack` mesmo em erro
  (linha 152-155, comentário "D12" no topo do arquivo), reproduzindo de propósito um
  comportamento do Java que engolia erro sem DLQ. Para os 2 consumers do ms-communication, o
  padrão correto de ack/nack/DLQ já está no próprio Java atual (`MessageSendRabbitMQConsumer`:
  ack só após sucesso, nack sem requeue para DLQ em erro de validação, nack com requeue para
  retry em outros erros) — é esse comportamento que deve ser preservado na migração, não o do
  ms-auth-go.
