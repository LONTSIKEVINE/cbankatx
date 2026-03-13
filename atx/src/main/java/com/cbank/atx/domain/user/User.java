package com.cbank.atx.domain.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "users")       // → collection "users" dans MongoDB
public class User {

    @Id
    private String id;

    private String firstname;          // prénom de l'agent
    private String lastname;           // nom de l'agent
    private String email;              // email professionnel

    private String branchId;           // référence → collection "branchs"
    private String profilId;           // référence → collection "profils"
    private String backupId;           // référence → un autre User (remplaçant)
    private String atxId;              // référence → collection "atxs"

    private Map<String, String> function; // fonctions spéciales de l'agent

    private Session session;           // ← session imbriquée directement

    private List<RequestAssignment> assignments
            = new ArrayList<>();       // ← historique des assignations imbriqué

    private Boolean active;            // compte actif ou désactivé
}