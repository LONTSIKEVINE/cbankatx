
package com.cbank.atx.repository;

import com.cbank.atx.domain.user.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository
        extends MongoRepository<Profile, String> {
    // Pas de méthodes custom — findAll() suffit
}
