package com.cbank.atx.domain.settings;

import com.cbank.atx.enums.DataSource;
import lombok.Data;

@Data
public class Field {
    private int range;
    private String label;
    private int size;
    private DataSource dataSource;
    private String value;
}