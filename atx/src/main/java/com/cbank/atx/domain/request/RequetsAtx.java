package com.cbank.atx.domain.request;

import com.cbank.atx.domain.user.UserAssignment;
import com.cbank.atx.enums.RequestStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "requets_atxs")
public class RequetsAtx {
    @Id
    private String id;
    private String reason;
    private String customer;
    private String accountNumber;
    private String atxId;
    private String assignedTo;
    private String createdBy;
    private Date requestedAt;
    private Date deliveredAt;
    private RequestStatus status;
    private List<UserAssignment> assignments = new ArrayList<>();
    private List<ParamValue> paramsValues = new ArrayList<>();
}