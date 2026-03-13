package com.cbank.atx.domain.branch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cities")      // → collection "cities" dans MongoDB
public class City {

    @Id
    private String id;

    private String label;              // "Yaoundé", "Douala", "Bafoussam"
    private int code;                  // 1, 2, 3...
}