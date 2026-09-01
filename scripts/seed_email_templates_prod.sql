-- Seed mínimo de templates de e-mail KeepGuard.
-- tenant_id na tabela = companyId usado pelo ms-communication (PK da empresa).
-- Empresa atual (KeepGuard Secondary): f7fc7350-b9fc-4e54-9c58-ac9385b23ae4

BEGIN;

-- Provider URL: serviço real em k8s/docker é srv-email-sender (não srv-email-google-sender)
UPDATE ms_communication.providers
SET url = 'http://srv-email-sender:8601',
    updated_at = NOW()
WHERE provider_type = 'EMAIL_GOOGLE_SENDER'
  AND (url IS NULL OR url LIKE '%srv-email-google-sender%');

-- Helpers: insert if missing for company ae4
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT gen_random_uuid(), 'AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN', 'EMAIL',
       'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
       'Código de verificação de dispositivo',
       'Código MFA enviado ao confirmar novo dispositivo',
       'KeepGuard - Confirmação de Novo Dispositivo',
       'Olá {{userName}},<br/><br/>Seu código de verificação para o dispositivo <b>{{deviceName}}</b> é:<br/><br/><h2 style="letter-spacing:4px;">{{token}}</h2><br/>Este código expira em {{expiresIn}} minutos.<br/><br/>Se não foi você, ignore este e-mail.',
       'token,expiresIn,deviceName,userName',
       true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM ms_communication.templates
  WHERE template_type = 'AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN'
    AND message_type = 'EMAIL'
    AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT gen_random_uuid(), 'NOVO_DISPOSITIVO_AUTENTICADO', 'EMAIL',
       'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
       'Alerta de novo dispositivo autenticado',
       'Alerta de segurança após autenticação em novo dispositivo, com link de revogação',
       'KeepGuard - Alerta de Segurança: Novo dispositivo conectado',
       'Olá {{userName}},<br/><br/>Um novo dispositivo conectou-se à sua conta KeepGuard.<br/><br/><b>Dispositivo:</b> {{deviceName}} ({{deviceType}})<br/><b>IP:</b> {{ipAddress}}<br/><b>Horário:</b> {{loginTime}}<br/><br/>Se foi você, nenhuma ação é necessária.<br/>Caso <b>NÃO RECONHEÇA</b>, revogue e bloqueie o dispositivo:<br/><br/><a href="{{revokeUrl}}" style="display:inline-block;padding:10px 20px;background-color:#d9534f;color:#ffffff;text-decoration:none;border-radius:5px;">Revogar e bloquear dispositivo</a><br/><br/>Ou acesse: {{revokeUrl}}',
       'userName,deviceName,deviceType,ipAddress,userAgent,loginTime,quickRevokeToken,revokeUrl',
       true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM ms_communication.templates
  WHERE template_type = 'NOVO_DISPOSITIVO_AUTENTICADO'
    AND message_type = 'EMAIL'
    AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT gen_random_uuid(), 'SENHA_ALTERADA_SUCESSO', 'EMAIL',
       'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
       'Notificação de senha alterada',
       'Alerta após troca/reset de senha com link de bloqueio',
       'KeepGuard - Alerta de Segurança: Senha alterada',
       'Olá {{userName}}, a senha da sua conta no {{appName}} foi alterada com sucesso.<br/><br/><b>Data/Hora:</b> {{updatedAt}}<br/><b>Dispositivo:</b> {{deviceName}} ({{deviceType}})<br/><b>IP:</b> {{ipAddress}}<br/><br/>Se foi você, nenhuma ação é necessária.<br/>Caso <b>NÃO RECONHEÇA</b>:<br/><br/><a href="{{revokeUrl}}" style="display:inline-block;padding:10px 20px;background-color:#d9534f;color:#ffffff;text-decoration:none;border-radius:5px;">Revogar e bloquear dispositivo</a><br/><br/>{{revokeUrl}}',
       'userName,appName,updatedAt,deviceName,deviceType,ipAddress,userAgent,quickRevokeToken,revokeUrl',
       true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM ms_communication.templates
  WHERE template_type = 'SENHA_ALTERADA_SUCESSO'
    AND message_type = 'EMAIL'
    AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT gen_random_uuid(), 'RECUPERACAO_SENHA', 'EMAIL',
       'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
       'Recuperação de senha',
       'Código/link de recuperação de senha',
       'KeepGuard - Recuperação de senha',
       'Olá {{userName}},<br/><br/>Recebemos um pedido de recuperação de senha.<br/><br/>Código: <b>{{token}}</b><br/><br/>Se não foi você, ignore este e-mail.',
       'userName,token',
       true, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM ms_communication.templates
  WHERE template_type = 'RECUPERACAO_SENHA'
    AND message_type = 'EMAIL'
    AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

COMMIT;
