package com.cbank.atx.domain.atx;

import com.cbank.atx.enums.SignaturePosition;
import lombok.Data;

@Data
public class Mandatory {
    private String userId;
    private SignaturePosition signaturePosition;
}