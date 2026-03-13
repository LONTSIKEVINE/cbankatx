package com.cbank.atx.repository;

import com.cbank.atx.domain.atx.Atx;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AtxRepository
        extends MongoRepository<Atx, String> {
    @Query("{ 'locales.fr.atxCode': ?0 }")
    Optional<Atx> findByAtxCode(String atxCode);
    @Query("{ 'locales.fr.deliverableInBranchs.branchId': ?0 }")
    List<Atx> findByBranchId(String branchId);
}