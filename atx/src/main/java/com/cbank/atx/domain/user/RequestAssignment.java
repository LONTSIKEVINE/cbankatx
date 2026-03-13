package com.cbank.atx.domain.user;

import com.cbank.atx.enums.RequestStatus;
import lombok.Data;
import java.util.Date;

@Data
//  Pas de @Document — imbriquée dans User
public class RequestAssignment {

    private String requestId;       // ID de la demande concernée
    private String assignmentId;    // ID unique de cette assignation
    private RequestStatus status;   // statut au moment de l'assignation
    private Date assignedAt;        // quand l'agent a reçu la demande
    private Date endedAt;           // quand l'agent a terminé
}