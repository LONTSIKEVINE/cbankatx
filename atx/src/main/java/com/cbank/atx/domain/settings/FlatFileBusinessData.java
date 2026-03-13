package com.cbank.atx.domain.settings;

import lombok.Data;

@Data
public class FlatFileBusinessData {
    private String rowBranch;
    private int rowAmount;
    private String rowSide;
    private String rowAccountNumber;
    private String rowUser;
    private String rowDate;
}