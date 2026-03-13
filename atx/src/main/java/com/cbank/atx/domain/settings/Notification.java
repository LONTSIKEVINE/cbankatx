package com.cbank.atx.domain.settings;

import lombok.Data;

@Data
public class Notification {
    private AppSwitch requestAssignment;
    private AppSwitch requestEndProcessing;
    private AppSwitch userAccountDesactivate;
    private AppSwitch userInvitation;
    private AppSwitch updateFlatFile;
    private AppSwitch flatFileTransferFailed;
}