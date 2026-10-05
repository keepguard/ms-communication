# ms-communication — Consumidores e Infraestrutura Real de Produção

Levantamento para subsidiar a migração Java → Go. Fontes: grep/leitura de código em
`achadinhos/`, `investbot/`, `keepguard-core/` e `kubectl get/describe/top` (somente leitura,
namespace `keepguard`, label `app=ms-communication`). Data do levantamento: 2026-10-05.

## 1. Consumidores HTTP confirmados (`POST /api/v1/messages/send`, header `X-Company-Id`)

Nenhum consumidor em `achadinhos/` ou `investbot/` (grep exaustivo, zero hits). Todos os
consumidores estão em `keepguard-core/`:

### 1.1 ms-ai-guardian (Java)
- Cliente: `CommunicationMessageClient.java:10-22` (Feign, timeout connect 2s / read 8s).
- Caller real: `EmailNotificationService.java`, fluxo `dispatchEmail` → `fallbackHttp`
  (linhas 248-291). **Só é chamado como fallback** se a publicação RabbitMQ
  (`publishToGoogleSender`) falhar — o caminho principal é fila, não HTTP.
- Payload montado: `companyId`, `correlationId`, `xCorrelationId`,
  `messageType="EMAIL"`, `recipient`, `templateType="ALERTA_SEGURANCA"`, `subject`,
  `communicationType="EMAIL"`, `codeUser="ADMIN_GUARDIAN"`,
  `variables={serviceName, diagnosticReportHtml}`.
- Resposta: descartada (não lida).
- Erro: logado, retorna `false`; sem retry, sem circuit breaker.

### 1.2 ms-auth (Java)
- Cliente: `CommunicationClient.java:13-30` (Feign).
- Caller real: `DeviceSessionCommandService.java:118,282,353` (OTP, novo dispositivo, senha
  alterada) — injeção direta, sem nenhuma resiliência.
- Existe `CommunicationClientAdapter.java` com CircuitBreaker/Retry/Bulkhead, mas é **código
  morto**: nunca é injetado para `sendMessage`, só expõe health/test.
- Resposta: descartada.
- Erro: try/catch que só loga — nunca propaga para o chamador original.

### 1.3 ms-auth-go
- Cliente: `internal/adapters/out/http/mscommunication/communication_client.go:28-44`.
  Comentário explícito no código: "POST sem retry (não é idempotente)".
- O cliente HTTP resiliente genérico do serviço tem circuit breaker, mas retry só se aplica
  a GET — POST para `/messages/send` nunca é re-tentado.
- Caller: `auth_command_usecase.go:201-206`, função `sendMessage` — erro é logado e engolido
  por design (decisão deliberada, não omissão).

### 1.4 ms-ai-guardian-go
- Cliente: `internal/adapters/out/http/mscommunication/communication_client.go:37-70`.
- Mesmo padrão do 1.1: fallback HTTP só se a publicação RabbitMQ falhar. Sem retry, sem
  header de Authorization, timeout 8s.
- Caller: `email_notifier.go:177-195`.

### 1.5 bff-auth (Go)
- Cliente: `communication_client.go:49-72`, via `resty`, **com retry real** (3x, wait 1s).
- Usado como fallback HTTP dentro do circuit-breaker decorator do `messagePublisher`
  (RabbitMQ é o caminho principal). O decorator não lê campos da resposta — só repassa erro.

### 1.6 bff-core (Go)
- Cliente: `communication_client.go:61-84`, via `resty`, retry 2x/200ms.
- Achado: injetado nos use cases de registro mas **nunca chamado diretamente** — o envio
  real é via `messagePublisher.PublishMessage` (RabbitMQ); o client HTTP só entra como
  fallback dentro do decorator. Erros de `PublishMessage` são descartados (`_ = ...`) nos
  use cases de registro.

**Padrão geral observado:** na maioria dos callers Go, RabbitMQ é o caminho primário e HTTP
é fallback só quando a fila falha; os campos de response nunca são lidos de fato (fire-and-
forget); erro é majoritariamente engolido/logado, não propagado ao usuário final.

## 2. Contratos fora do HTTP

- **Redis**: nenhuma chave (`template_cache`, `provider_cache`, `providers_by_type`,
  `billing:email:paid:*`, `billing:email:overdue:*`) é lida por qualquer outro serviço —
  uso é 100% interno ao ms-communication.
- **RabbitMQ `ms-billing-exchange-*`**: publicado só pelo `ms-billing`, consumido só pelo
  `ms-communication`. Nenhum terceiro consumidor encontrado.
- **ACHADO CRÍTICO — fila `keepguard.notifications.sms`**: é criada e publicada pelo
  `ms-communication` (durable, sem argumentos de DLX) e consumida pelo `srv-sms-sender`
  (`srv-sms-sender/internal/adapters/in/rabbitmq/consumer.go:70-72`; comentário no próprio
  código confirma: "a fila principal já é criada pelo ms-communication... não redeclara com
  DLX: RabbitMQ recusa PRECONDITION_FAILED se os argumentos diferirem"). O DTO do
  `srv-sms-sender` (`sms_dto.go:22-36`) documenta: "O MS Java (ms-communication) envia
  companyId; o contrato Go usa tenantId".
  **Esse é um contrato de fila que a migração Go precisa preservar byte a byte** — mesmo
  nome de fila, mesmos argumentos de durabilidade/DLX, mesmo campo `companyId` no payload.
  Qualquer divergência quebra o `srv-sms-sender` em produção sem aviso (PRECONDITION_FAILED
  no RabbitMQ, ou campo ausente no consumer).
- **`srv-audit-exchange-*`**: exchange de auditoria genérica usada por todo serviço (via
  `lib-common`/equivalente Go) — não é um contrato específico de consumo do
  ms-communication, é infraestrutura compartilhada de audit trail.

## 3. Deployment real em produção (K8s, namespace `keepguard`, leitura via kubectl)

- **Imagem**: `ghcr.io/keepguard/ms-communication:latest` — **não versionada por tag**;
  digest atual do pod rodando: `sha256:7c7ccad894a7c8681605538708ace10abb035ea87bbc9cc610052074cb0de923`.
- **Réplicas**: 1 (sem HA). Sem HPA, PodDisruptionBudget ou NetworkPolicy no namespace.
- **Resources**: apenas `memory` (`limits: 1Gi`, `requests: 384Mi`) — **sem** `cpu`
  requests/limits definidos.
- **Probes**: **sem** liveness/readiness configurados no Deployment.
- **Uso real observado** (`kubectl top`): CPU 2m, memória 745Mi (~73% do limite de 1Gi).
- **Env vars** (nomes, nunca valores de secret): `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/keepguard_api_db`,
  `SPRING_DATA_REDIS_HOST=redis:6379`, `SPRING_RABBITMQ_HOST=rabbitmq-service:5672`,
  `KEEPGUARD_AUDIT_EXCHANGE=srv-audit-exchange-prod`. Secrets referenciados: `JWT_SECRET`,
  `POSTGRES_PASSWORD` (do secret `keepguard-secret`).
- **Service**: ClusterIP, porta 8082 → 8082. Sem Ingress (correto — serviço interno).

## 4. ACHADO GRAVE — profile Spring ativo em produção é `local`, não `prod`

- `SPRING_PROFILES_ACTIVE` no Deployment vem do configmap `keepguard-config`
  (`kubectl get configmap keepguard-config -o jsonpath='{.data}'`), cujo valor real é
  **`"local"`**.
- `ms-communication` é o **único** serviço Java que delega essa variável ao configmap
  compartilhado — `ms-billing` usa valor literal `prod` e `ms-knowledge` usa `k8s`
  (confirmado via `kubectl get deployments -o json`, comparando os 3 envs).
- Consequência: em produção o serviço roda com `application-local.yml`
  (`src/main/resources/application-local.yml`), não `application-prod.yml`:
  - `hibernate.ddl-auto: update` em vez de `validate` (prod deveria ser `validate` por
    design, mas hoje está em modo de auto-migração de schema).
  - Filas/exchanges com sufixo de **dev** ativos em produção:
    `message-send: ms.communication.message.send.dev`,
    `message-exchange: ms-communication-exchange-dev`,
    `billing-exchange: ms-billing-exchange-dev` — em vez dos nomes `.prod`/`-prod`
    documentados em `application-prod.yml`.
  - **Implicação direta pra migração**: os nomes de fila efetivamente ativos em produção
    HOJE são os de dev, não os de prod. O serviço Go precisa apontar pros nomes reais em
    uso (dev) ou a migração precisa incluir a correção desse profile antes do cutover — do
    contrário o novo serviço Go pode publicar em filas que nada consome (se usar os nomes
    `-prod`) ou herdar o mesmo profile incorreto.

## 5. Outras configs estranhas encontradas

- **Credencial literal no Deployment**: `SPRING_RABBITMQ_USERNAME`/`SPRING_RABBITMQ_PASSWORD`
  = `guest`/`guest` como valor literal direto no manifest — não vêm do secret
  `keepguard-secret` (que já tem `RABBITMQ_DEFAULT_PASS` disponível e não é usado aqui).
- **Peculiaridade de config já conhecida** (ver `system-design.md`): no `application.yml`
  base, o bloco de conexão Rabbit aparece sob `management.rabbitmq` em vez de
  `spring.rabbitmq` — os perfis local/dev/prod corrigem isso corretamente, mas reforça que
  o arquivo base não deveria estar ativo em produção (ver item 4).

## 6. Observabilidade, schema e Ingress

- **Prometheus**: sem ServiceMonitor CRD no cluster (Prometheus Operator não instalado —
  `kubectl get servicemonitor` retorna "resource type not found"). Scrape é via
  `static_configs` em `keepguard-core/k8s/observability/prometheus-configmap.yaml`
  (`job_name: ms-communication`, target `ms-communication:8082`, path
  `/actuator/prometheus`, label `environment: production`).
- **Grafana**: dashboard dedicado existe, gerado pelo script
  `keepguard-core/k8s/observability/generate-dashboards.py`, output em
  `keepguard-core/k8s/observability/dashboards/ms-communication.json` (cópia espelhada em
  `keepguard-core/docker/grafana/provisioning/dashboards/json/ms-communication.json`).
- **Schema do banco** (`ms_communication`): sem Flyway, sem script DDL manual — só
  Hibernate `ddl-auto`, que por causa do item 4 está em `update` em produção (não
  `validate` como o design pretende).
- **Ingress**: nenhum para `ms-communication` — correto, é ClusterIP interno. Os únicos 3
  Ingress do namespace são `front-achadinhos-ingress`, `front-keepguard-core-ingress` e
  `grafana-ingress`.

## 7. Resumo de risco para a migração Go

1. Preservar **exatamente** o contrato da fila `keepguard.notifications.sms` (nome, args de
   durabilidade/DLX, campo `companyId`) — é o único contrato assíncrono com consumidor
   externo confirmado (`srv-sms-sender`).
2. Decidir, antes do cutover, quais nomes de fila/exchange o novo serviço vai usar — os
   `-dev` que estão realmente ativos hoje, ou corrigir o profile para `-prod` como parte da
   migração (nesse caso, atualizar `srv-sms-sender` e `ms-billing` juntos).
3. Nenhum consumidor HTTP depende de campos de response — a migração pode simplificar o
   contrato de saída sem medo de quebrar leitura de response em nenhum caller mapeado.
4. Maioria dos callers já trata falha de HTTP como best-effort (loga e segue) — a migração
   não herda pressão de disponibilidade alta desse lado, mas o caminho RabbitMQ (fila
   `keepguard.notifications.sms` e exchanges de billing) é o que não pode falhar
   silenciosamente.
