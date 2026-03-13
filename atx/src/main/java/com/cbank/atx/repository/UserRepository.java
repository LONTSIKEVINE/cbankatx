
package com.cbank.atx.repository;

import com.cbank.atx.domain.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends MongoRepository<User, String> {

    // Trouver un user par email
    // → utilisé pour la connexion
    Optional<User> findByEmail(String email);

    // Trouver tous les users d'une agence
    // → utilisé pour lister les agents d'une agence
    List<User> findByBranchId(String branchId);

    // Trouver tous les users actifs
    // → utilisé pour l'assignation des demandes
    List<User> findByActiveTrue();

    // Trouver les users actifs d'une agence
    // → utilisé pour assigner une demande
    List<User> findByBranchIdAndActiveTrue(String branchId);
}
