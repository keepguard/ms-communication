--
-- PostgreSQL database dump
--

-- Dumped from database version 15.5 (Debian 15.5-1.pgdg120+1)
-- Dumped by pg_dump version 15.5 (Debian 15.5-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: ms_communication; Type: SCHEMA; Schema: -; Owner: keepguard_api_user
--

CREATE SCHEMA ms_communication;


ALTER SCHEMA ms_communication OWNER TO keepguard_api_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: providers; Type: TABLE; Schema: ms_communication; Owner: keepguard_api_user
--

CREATE TABLE ms_communication.providers (
    id uuid NOT NULL,
    communication_type character varying(30) NOT NULL,
    configuration text,
    created_at timestamp(6) without time zone NOT NULL,
    daily_limit integer,
    is_active boolean NOT NULL,
    is_default boolean NOT NULL,
    max_retries integer,
    monthly_limit integer,
    name character varying(100) NOT NULL,
    priority integer NOT NULL,
    provider_type character varying(50) NOT NULL,
    rate_limit_per_minute integer,
    timeout_seconds integer,
    updated_at timestamp(6) without time zone NOT NULL,
    url character varying(500),
    variables jsonb,
    CONSTRAINT providers_communication_type_check CHECK (((communication_type)::text = ANY (ARRAY[('EMAIL'::character varying)::text, ('SMS'::character varying)::text, ('PUSH_NOTIFICATION'::character varying)::text, ('WHATSAPP'::character varying)::text, ('TELEGRAM'::character varying)::text, ('SENDGRID'::character varying)::text, ('PUSH'::character varying)::text]))),
    CONSTRAINT providers_provider_type_check CHECK (((provider_type)::text = ANY (ARRAY[('N8N'::character varying)::text, ('EMAIL_GOOGLE_SENDER'::character varying)::text, ('SENDGRID'::character varying)::text, ('SRV_SMS_SENDER'::character varying)::text])))
);


ALTER TABLE ms_communication.providers OWNER TO keepguard_api_user;

--
-- Name: templates; Type: TABLE; Schema: ms_communication; Owner: keepguard_api_user
--

CREATE TABLE ms_communication.templates (
    id uuid NOT NULL,
    content text NOT NULL,
    created_at timestamp(6) without time zone,
    description character varying(500) NOT NULL,
    is_active boolean NOT NULL,
    message_type character varying(20) NOT NULL,
    name character varying(200) NOT NULL,
    subject character varying(200),
    template_type character varying(50) NOT NULL,
    updated_at timestamp(6) without time zone,
    variables text,
    tenant_id character varying(100) NOT NULL,
    CONSTRAINT templates_message_type_check CHECK (((message_type)::text = ANY (ARRAY[('EMAIL'::character varying)::text, ('SMS'::character varying)::text, ('PUSH_NOTIFICATION'::character varying)::text, ('WHATSAPP'::character varying)::text, ('PUSH'::character varying)::text]))),
    CONSTRAINT templates_template_type_check CHECK (((template_type)::text = ANY (ARRAY[('AUTENTICACAO_EMAIL_TOKEN'::character varying)::text, ('AUTENTICACAO_EMAIL_TOKEN_RESEND'::character varying)::text, ('AUTENTICACAO_SMS_TOKEN'::character varying)::text, ('AUTENTICACAO_WHATSAPP_TOKEN'::character varying)::text, ('AUTENTICACAO_DISPOSITIVO_EMAIL_TOKEN'::character varying)::text, ('AUTENTICACAO_DISPOSITIVO_SMS_TOKEN'::character varying)::text, ('AUTENTICACAO_DISPOSITIVO_WHATSAPP_TOKEN'::character varying)::text, ('CADASTRO_SUCESSO'::character varying)::text, ('RECUPERACAO_SENHA'::character varying)::text, ('SENHA_ALTERADA_SUCESSO'::character varying)::text, ('NOVO_DISPOSITIVO_AUTENTICADO'::character varying)::text, ('NOTIFICACAO_GERAL'::character varying)::text, ('ALERTA_SEGURANCA'::character varying)::text, ('CONFIRMACAO_ACAO'::character varying)::text])))
);


ALTER TABLE ms_communication.templates OWNER TO keepguard_api_user;

--
-- Name: providers providers_pkey; Type: CONSTRAINT; Schema: ms_communication; Owner: keepguard_api_user
--

ALTER TABLE ONLY ms_communication.providers
    ADD CONSTRAINT providers_pkey PRIMARY KEY (id);


--
-- Name: templates templates_pkey; Type: CONSTRAINT; Schema: ms_communication; Owner: keepguard_api_user
--

ALTER TABLE ONLY ms_communication.templates
    ADD CONSTRAINT templates_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

