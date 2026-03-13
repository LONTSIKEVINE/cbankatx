package com.cbank.atx.repository;

import com.cbank.atx.domain.branch.Branch;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BranchRepository
        extends MongoRepository<Branch, String> {

    // Toutes les agences d'une ville
    List<Branch> findByCityId(String cityId);

    // Toutes les agences gérées par un manager
    List<Branch> findByManagerId(String managerId);

    // Vérifier si le code agence existe déjà
    boolean existsByCode(String code);
}
