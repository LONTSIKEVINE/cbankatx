package com.cbank.atx.dto;

import lombok.Data;

@Data
public class TwoFactorRequest {
    private String email;
    private String code;
}