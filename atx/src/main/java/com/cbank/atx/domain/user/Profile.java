package com.cbank.atx.domain.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data                                  // Lombok génère les getters/setters
@Document(collection = "profils")      // → collection "profils" dans MongoDB
public class Profile {

    @Id                                // → "_id" dans MongoDB
    private String id;

    private String label;              // "BackOffice Administrateur"
    private String code;               // "BO_ADMIN"
}