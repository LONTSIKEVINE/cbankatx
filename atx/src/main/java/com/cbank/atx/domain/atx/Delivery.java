package com.cbank.atx.domain.atx;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Delivery {
    private String branchId;
    private List<Mandatory> mandatories = new ArrayList<>();
}