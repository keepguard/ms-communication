-- OBSOLETO.
-- Este script foi substituído por update_email_templates_html.sql,
-- que atualiza TODOS os templates EMAIL (incluindo SENHA_ALTERADA_SUCESSO)
-- em todos os tenants, com o HTML KeepGuard.
--
-- Não execute este arquivo. Use:
--   scripts/update_email_templates_html.sql
--
-- Smoke P0 após o apply: AUTENTICACAO_EMAIL_TOKEN,
-- AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN, RECUPERACAO_SENHA,
-- CADASTRO_SUCESSO, NOVO_DISPOSITIVO_AUTENTICADO, SENHA_ALTERADA_SUCESSO.

SELECT 'OBSOLETO: execute scripts/update_email_templates_html.sql' AS aviso;
