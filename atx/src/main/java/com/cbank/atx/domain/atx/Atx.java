package com.cbank.atx.domain.atx;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "atxs")
public class Atx {
    @Id
    private String id;
    private Map<String, LocaleAtx> locales = new HashMap<>();
}