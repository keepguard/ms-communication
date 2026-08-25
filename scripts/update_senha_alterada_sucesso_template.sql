-- Atualiza o template SENHA_ALTERADA_SUCESSO com contexto de dispositivo e link de bloqueio (quick-revoke).
-- Tenant padrão KeepGuard local/dev.

UPDATE ms_communication.templates
SET
    subject = 'Alerta de Segurança: Senha da sua conta foi alterada',
    content = 'Olá {{userName}}, a senha da sua conta no {{appName}} foi alterada com sucesso.<br/><br/><b>Data/Hora:</b> {{updatedAt}}<br/><b>Dispositivo:</b> {{deviceName}} ({{deviceType}})<br/><b>Endereço IP:</b> {{ipAddress}}<br/><br/>Se foi você, nenhuma ação é necessária.<br/>Caso <b>NÃO RECONHEÇA</b> esta alteração, clique no link abaixo para revogar a sessão e bloquear este dispositivo imediatamente:<br/><br/><a href="{{revokeUrl}}" style="display:inline-block;padding:10px 20px;background-color:#d9534f;color:#ffffff;text-decoration:none;border-radius:5px;">Não reconheço esta alteração (Revogar e Bloquear)</a><br/><br/>Ou acesse o link direto: {{revokeUrl}}',
    variables = 'userName,appName,updatedAt,deviceName,deviceType,ipAddress,userAgent,quickRevokeToken,revokeUrl',
    updated_at = NOW()
WHERE template_type = 'SENHA_ALTERADA_SUCESSO'
  AND message_type = 'EMAIL'
  AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3';

-- Se o template ainda não existir para o tenant, cria.
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    'SENHA_ALTERADA_SUCESSO',
    'EMAIL',
    'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Notificação de Senha Alterada com Sucesso',
    'Alerta de segurança enviado após troca ou reset de senha, com link de bloqueio do dispositivo',
    'Alerta de Segurança: Senha da sua conta foi alterada',
    'Olá {{userName}}, a senha da sua conta no {{appName}} foi alterada com sucesso.<br/><br/><b>Data/Hora:</b> {{updatedAt}}<br/><b>Dispositivo:</b> {{deviceName}} ({{deviceType}})<br/><b>Endereço IP:</b> {{ipAddress}}<br/><br/>Se foi você, nenhuma ação é necessária.<br/>Caso <b>NÃO RECONHEÇA</b> esta alteração, clique no link abaixo para revogar a sessão e bloquear este dispositivo imediatamente:<br/><br/><a href="{{revokeUrl}}" style="display:inline-block;padding:10px 20px;background-color:#d9534f;color:#ffffff;text-decoration:none;border-radius:5px;">Não reconheço esta alteração (Revogar e Bloquear)</a><br/><br/>Ou acesse o link direto: {{revokeUrl}}',
    'userName,appName,updatedAt,deviceName,deviceType,ipAddress,userAgent,quickRevokeToken,revokeUrl',
    true,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM ms_communication.templates
    WHERE template_type = 'SENHA_ALTERADA_SUCESSO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);
