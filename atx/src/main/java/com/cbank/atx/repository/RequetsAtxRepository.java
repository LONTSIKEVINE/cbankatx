package com.cbank.atx.repository;

import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.enums.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RequetsAtxRepository
        extends MongoRepository<RequetsAtx, String> {
    List<RequetsAtx> findByAssignedTo(String userId);
    List<RequetsAtx> findByStatus(RequestStatus status);
    List<RequetsAtx> findByCustomer(String customer);
    List<RequetsAtx> findByCreatedBy(String userId);
    List<RequetsAtx> findByAssignedToAndStatus(
            String userId, RequestStatus status);
}