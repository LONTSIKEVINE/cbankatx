package com.cbank.atx.domain.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String password; // ← simple, sans annotation
    private String branchId;
    private String profilId;
    private String backupId;
    private String atxId;
    private Map<String, String> function;
    private Session session;
    private List<RequestAssignment> assignments
            = new ArrayList<>();
    private Boolean active;
}