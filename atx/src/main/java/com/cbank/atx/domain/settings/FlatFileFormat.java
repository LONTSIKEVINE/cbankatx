
package com.cbank.atx.domain.settings;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class FlatFileFormat {
    private List<Field> fields = new ArrayList<>();
    private String name;
    private String delimiter;
    private Boolean fillFieldLength;
}