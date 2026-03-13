package com.cbank.atx.domain.atx;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class LocaleAtx {
    private String label;
    private String templateUrl;
    private String atxCode;
    private int price;
    private boolean taxable;
    private int taxePercentage;
    private List<Delivery> deliverableInBranchs = new ArrayList<>();
    private List<Param> params = new ArrayList<>();
}