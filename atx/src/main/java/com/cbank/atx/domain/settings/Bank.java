package com.cbank.atx.domain.settings;

import lombok.Data;

@Data
public class Bank {
    private String dbmsServer;
    private String dbmsName;
    private String dbmsUsername;
    private String dbmsPassword;
}