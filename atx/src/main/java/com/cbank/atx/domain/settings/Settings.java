package com.cbank.atx.domain.settings;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "settings")
public class Settings {
    @Id
    private String id = "settings";
    private Notification notifications;
    private SecuritySettings security;
    private Bank bank;
    private BranchAssignment branchAssignments;
    private FlatFileFormat flatFileFormat;
    private FlatFileBusinessData flatFileBusinessData;
}