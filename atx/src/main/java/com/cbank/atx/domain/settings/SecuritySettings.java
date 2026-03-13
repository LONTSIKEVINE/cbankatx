package com.cbank.atx.domain.settings;

import lombok.Data;

@Data
public class SecuritySettings {
    private String ldapSsoApiKey;
    private String ldapSsoApiSecret;
    private boolean is2FA;
    private String cftApiKey;
    private String cftApiSecret;
}