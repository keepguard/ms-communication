-- Templates EMAIL HTML KeepGuard.
-- Fonte dos documentos: scripts/email-templates/*.html
-- SMS / WhatsApp: não alterar.
--
-- Aplicação
--   1. Dev: psql no schema ms_communication → este arquivo.
--   2. Smoke P0 (um envio real por tipo):
--        AUTENTICACAO_EMAIL_TOKEN
--        AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN
--        RECUPERACAO_SENHA
--        CADASTRO_SUCESSO
--        NOVO_DISPOSITIVO_AUTENTICADO
--        SENHA_ALTERADA_SUCESSO
--   3. Prod: o mesmo SQL (UPDATE cobre qualquer tenant_id já existente).
--   4. Conferir Gmail/Apple Mail: código legível, botão revogar clicável,
--      fallback text/plain (stripTags) não vira lixo.
--
-- Tenants com INSERT se ausente:
--   ae3 f7fc7350-b9fc-4e54-9c58-ac9385b23ae3 (local/dev)
--   ae4 f7fc7350-b9fc-4e54-9c58-ac9385b23ae4 (KeepGuard Secondary)
--
-- Substitui scripts/update_senha_alterada_sucesso_template.sql (obsoleto).
-- Cache Redis de template não é lido no envio; restart não é obrigatório.

BEGIN;

-- AUTENTICACAO_EMAIL_TOKEN
UPDATE ms_communication.templates
SET
    name = 'Código de verificação de cadastro',
    description = 'Código enviado ao iniciar o cadastro por e-mail',
    subject = 'KeepGuard - Seu código de verificação',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Seu código de verificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Seu código de verificação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, use o código abaixo para continuar o cadastro no {{appName}}.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se você não solicitou este cadastro, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,token,expiresIn,appName',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'AUTENTICACAO_EMAIL_TOKEN'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'AUTENTICACAO_EMAIL_TOKEN', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Código de verificação de cadastro', 'Código enviado ao iniciar o cadastro por e-mail', 'KeepGuard - Seu código de verificação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Seu código de verificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Seu código de verificação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, use o código abaixo para continuar o cadastro no {{appName}}.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se você não solicitou este cadastro, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,token,expiresIn,appName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'AUTENTICACAO_EMAIL_TOKEN'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'AUTENTICACAO_EMAIL_TOKEN', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Código de verificação de cadastro', 'Código enviado ao iniciar o cadastro por e-mail', 'KeepGuard - Seu código de verificação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Seu código de verificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Seu código de verificação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, use o código abaixo para continuar o cadastro no {{appName}}.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se você não solicitou este cadastro, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,token,expiresIn,appName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'AUTENTICACAO_EMAIL_TOKEN'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- AUTENTICACAO_EMAIL_TOKEN_RESEND
UPDATE ms_communication.templates
SET
    name = 'Reenvio do código de verificação de cadastro',
    description = 'Novo código enviado ao reenviar a verificação de cadastro',
    subject = 'KeepGuard - Novo código de verificação',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Novo código de verificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Novo código de verificação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, você pediu um novo código para continuar o cadastro no {{appName}}. O código anterior deixa de valer.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se você não solicitou este reenvio, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,token,expiresIn,appName',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'AUTENTICACAO_EMAIL_TOKEN_RESEND'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'AUTENTICACAO_EMAIL_TOKEN_RESEND', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Reenvio do código de verificação de cadastro', 'Novo código enviado ao reenviar a verificação de cadastro', 'KeepGuard - Novo código de verificação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Novo código de verificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Novo código de verificação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, você pediu um novo código para continuar o cadastro no {{appName}}. O código anterior deixa de valer.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se você não solicitou este reenvio, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,token,expiresIn,appName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'AUTENTICACAO_EMAIL_TOKEN_RESEND'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'AUTENTICACAO_EMAIL_TOKEN_RESEND', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Reenvio do código de verificação de cadastro', 'Novo código enviado ao reenviar a verificação de cadastro', 'KeepGuard - Novo código de verificação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Novo código de verificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Novo código de verificação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, você pediu um novo código para continuar o cadastro no {{appName}}. O código anterior deixa de valer.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se você não solicitou este reenvio, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,token,expiresIn,appName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'AUTENTICACAO_EMAIL_TOKEN_RESEND'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN
UPDATE ms_communication.templates
SET
    name = 'Código de verificação de dispositivo',
    description = 'Código MFA enviado ao confirmar novo dispositivo',
    subject = 'KeepGuard - Confirmação de novo dispositivo',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Confirmação de novo dispositivo</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Confirme este dispositivo</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, use o código abaixo para confirmar o dispositivo <strong style="color:#1d2129;">{{deviceName}}</strong>.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se não foi você, ignore este e-mail. Ninguém da KeepGuard pede este código.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'token,expiresIn,deviceName,userName',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Código de verificação de dispositivo', 'Código MFA enviado ao confirmar novo dispositivo', 'KeepGuard - Confirmação de novo dispositivo', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Confirmação de novo dispositivo</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Confirme este dispositivo</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, use o código abaixo para confirmar o dispositivo <strong style="color:#1d2129;">{{deviceName}}</strong>.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se não foi você, ignore este e-mail. Ninguém da KeepGuard pede este código.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'token,expiresIn,deviceName,userName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Código de verificação de dispositivo', 'Código MFA enviado ao confirmar novo dispositivo', 'KeepGuard - Confirmação de novo dispositivo', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Confirmação de novo dispositivo</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Confirme este dispositivo</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, use o código abaixo para confirmar o dispositivo <strong style="color:#1d2129;">{{deviceName}}</strong>.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Este código expira em {{expiresIn}} minutos. Se não foi você, ignore este e-mail. Ninguém da KeepGuard pede este código.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'token,expiresIn,deviceName,userName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- RECUPERACAO_SENHA
UPDATE ms_communication.templates
SET
    name = 'Recuperação de senha',
    description = 'Código de recuperação de senha',
    subject = 'KeepGuard - Recuperação de senha',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Recuperação de senha</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Recuperação de senha</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, recebemos um pedido para redefinir a senha da sua conta. Use o código abaixo para continuar.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não pediu a recuperação de senha, ignore este e-mail. Sua senha permanece a mesma.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,token',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'RECUPERACAO_SENHA'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'RECUPERACAO_SENHA', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Recuperação de senha', 'Código de recuperação de senha', 'KeepGuard - Recuperação de senha', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Recuperação de senha</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Recuperação de senha</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, recebemos um pedido para redefinir a senha da sua conta. Use o código abaixo para continuar.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não pediu a recuperação de senha, ignore este e-mail. Sua senha permanece a mesma.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,token',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'RECUPERACAO_SENHA'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'RECUPERACAO_SENHA', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Recuperação de senha', 'Código de recuperação de senha', 'KeepGuard - Recuperação de senha', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Recuperação de senha</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Recuperação de senha</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, recebemos um pedido para redefinir a senha da sua conta. Use o código abaixo para continuar.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f0ecfc;border:1px solid #dcd2f9;">
                <tr>
                  <td align="center" style="padding:20px 16px;">
                    <p style="margin:0;font-size:28px;line-height:1.2;font-weight:700;letter-spacing:6px;color:#1d2129;">{{token}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não pediu a recuperação de senha, ignore este e-mail. Sua senha permanece a mesma.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,token',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'RECUPERACAO_SENHA'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- CADASTRO_SUCESSO
UPDATE ms_communication.templates
SET
    name = 'Boas-vindas após cadastro',
    description = 'Confirmação de conta criada com sucesso',
    subject = 'KeepGuard - Conta criada com sucesso',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Conta criada</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Conta criada com sucesso</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, sua conta no {{appName}} está pronta. Você já pode entrar e continuar de onde parou.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#e6f7f3;border-left:4px solid #00b090;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0;font-size:14px;line-height:1.6;color:#1d2129;">Próximo passo: entre com o e-mail e a senha que você cadastrou. Guarde suas credenciais e ative a verificação em dois fatores quando disponível.</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não criou esta conta, ignore este e-mail ou fale com o suporte da sua empresa.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,appName',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'CADASTRO_SUCESSO'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'CADASTRO_SUCESSO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Boas-vindas após cadastro', 'Confirmação de conta criada com sucesso', 'KeepGuard - Conta criada com sucesso', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Conta criada</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Conta criada com sucesso</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, sua conta no {{appName}} está pronta. Você já pode entrar e continuar de onde parou.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#e6f7f3;border-left:4px solid #00b090;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0;font-size:14px;line-height:1.6;color:#1d2129;">Próximo passo: entre com o e-mail e a senha que você cadastrou. Guarde suas credenciais e ative a verificação em dois fatores quando disponível.</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não criou esta conta, ignore este e-mail ou fale com o suporte da sua empresa.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,appName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'CADASTRO_SUCESSO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'CADASTRO_SUCESSO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Boas-vindas após cadastro', 'Confirmação de conta criada com sucesso', 'KeepGuard - Conta criada com sucesso', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Conta criada</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Conta criada com sucesso</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, sua conta no {{appName}} está pronta. Você já pode entrar e continuar de onde parou.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#e6f7f3;border-left:4px solid #00b090;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0;font-size:14px;line-height:1.6;color:#1d2129;">Próximo passo: entre com o e-mail e a senha que você cadastrou. Guarde suas credenciais e ative a verificação em dois fatores quando disponível.</p>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não criou esta conta, ignore este e-mail ou fale com o suporte da sua empresa.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,appName',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'CADASTRO_SUCESSO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- NOVO_DISPOSITIVO_AUTENTICADO
UPDATE ms_communication.templates
SET
    name = 'Alerta de novo dispositivo autenticado',
    description = 'Alerta de segurança após autenticação em novo dispositivo, com link de revogação',
    subject = 'KeepGuard - Alerta de segurança: novo dispositivo conectado',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Novo dispositivo conectado</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Novo dispositivo conectado</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, um novo dispositivo autenticou na sua conta KeepGuard. Se foi você, nenhuma ação é necessária.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Dispositivo:</strong> {{deviceName}} ({{deviceType}})</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">IP:</strong> {{ipAddress}}</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Horário:</strong> {{loginTime}}</p>
                    <p style="margin:0;font-size:13px;line-height:1.6;color:#5f6368;"><strong style="color:#1d2129;">Navegador:</strong> {{userAgent}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:20px 0 12px 0;font-size:15px;line-height:1.6;color:#1d2129;font-weight:700;">Não reconhece este acesso?</p>
              <p style="margin:0 0 16px 0;font-size:14px;line-height:1.6;color:#3c4043;">Revogue a sessão e bloqueie o dispositivo imediatamente.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#eb1e3a;border-radius:8px;">
                    <a href="{{revokeUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Revogar e bloquear dispositivo</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{revokeUrl}}</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,deviceName,deviceType,ipAddress,userAgent,loginTime,quickRevokeToken,revokeUrl',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'NOVO_DISPOSITIVO_AUTENTICADO'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'NOVO_DISPOSITIVO_AUTENTICADO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Alerta de novo dispositivo autenticado', 'Alerta de segurança após autenticação em novo dispositivo, com link de revogação', 'KeepGuard - Alerta de segurança: novo dispositivo conectado', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Novo dispositivo conectado</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Novo dispositivo conectado</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, um novo dispositivo autenticou na sua conta KeepGuard. Se foi você, nenhuma ação é necessária.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Dispositivo:</strong> {{deviceName}} ({{deviceType}})</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">IP:</strong> {{ipAddress}}</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Horário:</strong> {{loginTime}}</p>
                    <p style="margin:0;font-size:13px;line-height:1.6;color:#5f6368;"><strong style="color:#1d2129;">Navegador:</strong> {{userAgent}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:20px 0 12px 0;font-size:15px;line-height:1.6;color:#1d2129;font-weight:700;">Não reconhece este acesso?</p>
              <p style="margin:0 0 16px 0;font-size:14px;line-height:1.6;color:#3c4043;">Revogue a sessão e bloqueie o dispositivo imediatamente.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#eb1e3a;border-radius:8px;">
                    <a href="{{revokeUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Revogar e bloquear dispositivo</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{revokeUrl}}</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,deviceName,deviceType,ipAddress,userAgent,loginTime,quickRevokeToken,revokeUrl',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'NOVO_DISPOSITIVO_AUTENTICADO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'NOVO_DISPOSITIVO_AUTENTICADO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Alerta de novo dispositivo autenticado', 'Alerta de segurança após autenticação em novo dispositivo, com link de revogação', 'KeepGuard - Alerta de segurança: novo dispositivo conectado', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Novo dispositivo conectado</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Novo dispositivo conectado</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, um novo dispositivo autenticou na sua conta KeepGuard. Se foi você, nenhuma ação é necessária.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Dispositivo:</strong> {{deviceName}} ({{deviceType}})</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">IP:</strong> {{ipAddress}}</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Horário:</strong> {{loginTime}}</p>
                    <p style="margin:0;font-size:13px;line-height:1.6;color:#5f6368;"><strong style="color:#1d2129;">Navegador:</strong> {{userAgent}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:20px 0 12px 0;font-size:15px;line-height:1.6;color:#1d2129;font-weight:700;">Não reconhece este acesso?</p>
              <p style="margin:0 0 16px 0;font-size:14px;line-height:1.6;color:#3c4043;">Revogue a sessão e bloqueie o dispositivo imediatamente.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#eb1e3a;border-radius:8px;">
                    <a href="{{revokeUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Revogar e bloquear dispositivo</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{revokeUrl}}</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,deviceName,deviceType,ipAddress,userAgent,loginTime,quickRevokeToken,revokeUrl',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'NOVO_DISPOSITIVO_AUTENTICADO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- SENHA_ALTERADA_SUCESSO
UPDATE ms_communication.templates
SET
    name = 'Notificação de senha alterada',
    description = 'Alerta após troca ou reset de senha, com link de bloqueio do dispositivo',
    subject = 'KeepGuard - Alerta de segurança: senha alterada',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Senha alterada</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Senha da sua conta foi alterada</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, a senha da sua conta no {{appName}} foi alterada. Se foi você, nenhuma ação é necessária.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Data/hora:</strong> {{updatedAt}}</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Dispositivo:</strong> {{deviceName}} ({{deviceType}})</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">IP:</strong> {{ipAddress}}</p>
                    <p style="margin:0;font-size:13px;line-height:1.6;color:#5f6368;"><strong style="color:#1d2129;">Navegador:</strong> {{userAgent}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:20px 0 12px 0;font-size:15px;line-height:1.6;color:#1d2129;font-weight:700;">Não reconhece esta alteração?</p>
              <p style="margin:0 0 16px 0;font-size:14px;line-height:1.6;color:#3c4043;">Revogue a sessão e bloqueie o dispositivo imediatamente.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#eb1e3a;border-radius:8px;">
                    <a href="{{revokeUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Revogar e bloquear dispositivo</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{revokeUrl}}</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,appName,updatedAt,deviceName,deviceType,ipAddress,userAgent,quickRevokeToken,revokeUrl',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'SENHA_ALTERADA_SUCESSO'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'SENHA_ALTERADA_SUCESSO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Notificação de senha alterada', 'Alerta após troca ou reset de senha, com link de bloqueio do dispositivo', 'KeepGuard - Alerta de segurança: senha alterada', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Senha alterada</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Senha da sua conta foi alterada</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, a senha da sua conta no {{appName}} foi alterada. Se foi você, nenhuma ação é necessária.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Data/hora:</strong> {{updatedAt}}</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Dispositivo:</strong> {{deviceName}} ({{deviceType}})</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">IP:</strong> {{ipAddress}}</p>
                    <p style="margin:0;font-size:13px;line-height:1.6;color:#5f6368;"><strong style="color:#1d2129;">Navegador:</strong> {{userAgent}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:20px 0 12px 0;font-size:15px;line-height:1.6;color:#1d2129;font-weight:700;">Não reconhece esta alteração?</p>
              <p style="margin:0 0 16px 0;font-size:14px;line-height:1.6;color:#3c4043;">Revogue a sessão e bloqueie o dispositivo imediatamente.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#eb1e3a;border-radius:8px;">
                    <a href="{{revokeUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Revogar e bloquear dispositivo</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{revokeUrl}}</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,appName,updatedAt,deviceName,deviceType,ipAddress,userAgent,quickRevokeToken,revokeUrl',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'SENHA_ALTERADA_SUCESSO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'SENHA_ALTERADA_SUCESSO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Notificação de senha alterada', 'Alerta após troca ou reset de senha, com link de bloqueio do dispositivo', 'KeepGuard - Alerta de segurança: senha alterada', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Senha alterada</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Senha da sua conta foi alterada</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, a senha da sua conta no {{appName}} foi alterada. Se foi você, nenhuma ação é necessária.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Data/hora:</strong> {{updatedAt}}</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">Dispositivo:</strong> {{deviceName}} ({{deviceType}})</p>
                    <p style="margin:0 0 8px 0;font-size:14px;line-height:1.6;color:#3c4043;"><strong style="color:#1d2129;">IP:</strong> {{ipAddress}}</p>
                    <p style="margin:0;font-size:13px;line-height:1.6;color:#5f6368;"><strong style="color:#1d2129;">Navegador:</strong> {{userAgent}}</p>
                  </td>
                </tr>
              </table>
              <p style="margin:20px 0 12px 0;font-size:15px;line-height:1.6;color:#1d2129;font-weight:700;">Não reconhece esta alteração?</p>
              <p style="margin:0 0 16px 0;font-size:14px;line-height:1.6;color:#3c4043;">Revogue a sessão e bloqueie o dispositivo imediatamente.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#eb1e3a;border-radius:8px;">
                    <a href="{{revokeUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Revogar e bloquear dispositivo</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{revokeUrl}}</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,appName,updatedAt,deviceName,deviceType,ipAddress,userAgent,quickRevokeToken,revokeUrl',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'SENHA_ALTERADA_SUCESSO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- ALERTA_SEGURANCA
UPDATE ms_communication.templates
SET
    name = 'Alerta de segurança',
    description = 'Fallback HTML para alertas do Guardian (serviceName + diagnosticReportHtml)',
    subject = 'KeepGuard - Alerta de segurança',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Alerta de segurança</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Alerta de segurança</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Foi detectado um evento que precisa de atenção no serviço <strong style="color:#1d2129;">{{serviceName}}</strong>.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;font-size:14px;line-height:1.6;color:#3c4043;">
                    {{diagnosticReportHtml}}
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'serviceName,diagnosticReportHtml',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'ALERTA_SEGURANCA'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'ALERTA_SEGURANCA', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Alerta de segurança', 'Fallback HTML para alertas do Guardian (serviceName + diagnosticReportHtml)', 'KeepGuard - Alerta de segurança', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Alerta de segurança</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Alerta de segurança</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Foi detectado um evento que precisa de atenção no serviço <strong style="color:#1d2129;">{{serviceName}}</strong>.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;font-size:14px;line-height:1.6;color:#3c4043;">
                    {{diagnosticReportHtml}}
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'serviceName,diagnosticReportHtml',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'ALERTA_SEGURANCA'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'ALERTA_SEGURANCA', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Alerta de segurança', 'Fallback HTML para alertas do Guardian (serviceName + diagnosticReportHtml)', 'KeepGuard - Alerta de segurança', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Alerta de segurança</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Alerta de segurança</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Foi detectado um evento que precisa de atenção no serviço <strong style="color:#1d2129;">{{serviceName}}</strong>.</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border:1px solid #e3e5e8;">
                <tr>
                  <td style="padding:14px 16px;font-size:14px;line-height:1.6;color:#3c4043;">
                    {{diagnosticReportHtml}}
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'serviceName,diagnosticReportHtml',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'ALERTA_SEGURANCA'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- NOTIFICACAO_GERAL
UPDATE ms_communication.templates
SET
    name = 'Notificação geral',
    description = 'Shell genérico de notificação por e-mail',
    subject = 'KeepGuard - Notificação',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Notificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Notificação</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}},</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border-left:4px solid #673de6;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0;font-size:15px;line-height:1.6;color:#3c4043;">{{message}}</p>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,message',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'NOTIFICACAO_GERAL'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'NOTIFICACAO_GERAL', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Notificação geral', 'Shell genérico de notificação por e-mail', 'KeepGuard - Notificação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Notificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Notificação</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}},</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border-left:4px solid #673de6;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0;font-size:15px;line-height:1.6;color:#3c4043;">{{message}}</p>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,message',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'NOTIFICACAO_GERAL'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'NOTIFICACAO_GERAL', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Notificação geral', 'Shell genérico de notificação por e-mail', 'KeepGuard - Notificação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Notificação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Notificação</h1>
              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}},</p>
              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f8f9fa;border-left:4px solid #673de6;">
                <tr>
                  <td style="padding:14px 16px;">
                    <p style="margin:0;font-size:15px;line-height:1.6;color:#3c4043;">{{message}}</p>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,message',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'NOTIFICACAO_GERAL'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);

-- CONFIRMACAO_ACAO
UPDATE ms_communication.templates
SET
    name = 'Confirmação de ação',
    description = 'Shell genérico para confirmar uma ação via link',
    subject = 'KeepGuard - Confirme esta ação',
    content = $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Confirme esta ação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Confirme esta ação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, precisamos da sua confirmação para: <strong style="color:#1d2129;">{{actionName}}</strong>.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#673de6;border-radius:8px;">
                    <a href="{{actionUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Confirmar ação</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{actionUrl}}</p>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não solicitou esta ação, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$,
    variables = 'userName,actionName,actionUrl',
    is_active = true,
    updated_at = NOW()
WHERE template_type = 'CONFIRMACAO_ACAO'
  AND message_type = 'EMAIL';

-- INSERT se ausente | KeepGuard local/dev (ae3)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'CONFIRMACAO_ACAO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3',
    'Confirmação de ação', 'Shell genérico para confirmar uma ação via link', 'KeepGuard - Confirme esta ação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Confirme esta ação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Confirme esta ação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, precisamos da sua confirmação para: <strong style="color:#1d2129;">{{actionName}}</strong>.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#673de6;border-radius:8px;">
                    <a href="{{actionUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Confirmar ação</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{actionUrl}}</p>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não solicitou esta ação, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,actionName,actionUrl',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'CONFIRMACAO_ACAO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae3'
);

-- INSERT se ausente | KeepGuard Secondary (ae4)
INSERT INTO ms_communication.templates (
    id, template_type, message_type, tenant_id, name, description, subject, content, variables, is_active, created_at, updated_at
)
SELECT
    gen_random_uuid(), 'CONFIRMACAO_ACAO', 'EMAIL', 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4',
    'Confirmação de ação', 'Shell genérico para confirmar uma ação via link', 'KeepGuard - Confirme esta ação', $kg_html$
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>KeepGuard - Confirme esta ação</title>
</head>
<body style="margin:0;padding:0;background-color:#f4f5f6;font-family:Arial,Helvetica,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f5f6;">
    <tr>
      <td align="center" style="padding:24px 16px;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%;background-color:#ffffff;border:1px solid #e3e5e8;">
          <tr>
            <td style="background-color:#673de6;padding:20px 24px;">
              <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:0.4px;">KEEP<span style="font-weight:600;opacity:0.9;">GUARD</span></p>
            </td>
          </tr>
          <tr>
            <td style="padding:28px 24px;">
              <h1 style="margin:0 0 12px 0;font-size:20px;line-height:1.3;color:#1d2129;font-weight:700;">Confirme esta ação</h1>
              <p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;color:#3c4043;">Olá {{userName}}, precisamos da sua confirmação para: <strong style="color:#1d2129;">{{actionName}}</strong>.</p>
              <table role="presentation" cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td align="center" style="background-color:#673de6;border-radius:8px;">
                    <a href="{{actionUrl}}" style="display:inline-block;padding:12px 24px;font-size:14px;font-weight:700;color:#ffffff;text-decoration:none;">Confirmar ação</a>
                  </td>
                </tr>
              </table>
              <p style="margin:16px 0 0 0;font-size:12px;line-height:1.6;color:#80868b;word-break:break-all;">Se o botão não funcionar, acesse:<br>{{actionUrl}}</p>
              <p style="margin:16px 0 0 0;font-size:14px;line-height:1.6;color:#5f6368;">Se você não solicitou esta ação, ignore este e-mail.</p>
            </td>
          </tr>
          <tr>
            <td style="background-color:#f8f9fa;padding:16px 24px;border-top:1px solid #e3e5e8;">
              <p style="margin:0;font-size:12px;line-height:1.5;color:#80868b;text-align:center;">Este é um e-mail automático · KeepGuard</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
$kg_html$, 'userName,actionName,actionUrl',
    true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM ms_communication.templates
    WHERE template_type = 'CONFIRMACAO_ACAO'
      AND message_type = 'EMAIL'
      AND tenant_id = 'f7fc7350-b9fc-4e54-9c58-ac9385b23ae4'
);
COMMIT;
