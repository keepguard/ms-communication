package com.keepguard.ms_communication.domain.enums;

public enum ProviderTypeEnum {
    N8N("n8n"),
    EMAIL_GOOGLE_SENDER("srv-email-google-sender"),
    SENDGRID("sendgrid"),
    SRV_SMS_SENDER("srv-sms-sender");
    private final String displayName;

    ProviderTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
