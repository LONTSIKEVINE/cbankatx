package com.cbank.atx.domain.branch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "branchs")     // → collection "branchs" dans MongoDB
public class Branch {

    @Id
    private String id;

    private String label;              // "Agence Bastos"
    private String code;               // "AG-001"
    private String taxesAccount;       // numéro compte taxes de l'agence
    private String productsAccount;    // numéro compte produits de l'agence
    private String managerId;          // référence → Users (le directeur)
    private String cityId;             // référence → Cities
}