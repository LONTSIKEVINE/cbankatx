package com.cbank.atx.domain.settings;

import lombok.Data;

@Data
public class BranchAssignment {
    private Boolean boCanAssignBoOtherBran;
    private Boolean assignBackupWhnStarterAbsent;
}