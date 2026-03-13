package com.cbank.atx.repository;

import com.cbank.atx.domain.settings.Settings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SettingsRepository
        extends MongoRepository<Settings, String> {
}