package com.cbank.atx.repository;

import com.cbank.atx.domain.branch.City;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CityRepository
        extends MongoRepository<City, String> {

    // Vérifier si une ville avec ce code existe déjà
    boolean existsByCode(int code);

    // Trouver une ville par son label
    Optional<City> findByLabel(String label);
}



