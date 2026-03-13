package com.cbank.atx.domain.user;

import com.cbank.atx.enums.RequestStatus;
import lombok.Data;
import java.util.Date;

@Data
public class UserAssignment {
    private String userId;
    private RequestStatus status;
    private Date assignedAt;
    private Date endedAt;
}