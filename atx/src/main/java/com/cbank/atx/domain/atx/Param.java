package com.cbank.atx.domain.atx;

import com.cbank.atx.enums.DataSource;
import com.cbank.atx.enums.ParamType;
import lombok.Data;

@Data
public class Param {
    private String name;
    private ParamType type;
    private DataSource dataSource;
    private String dataSourceValue;
    private String rawTemplateVariable;
}