package com.keepguard.ms_communication.domain.enums;

public enum ProviderTypeEnum {
    N8N("n8n"),
    EMAIL_GOOGLE_SENDER("srv-email-google-sender"),
    SENDGRID("sendgrid");
    private final String displayName;

    ProviderTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
