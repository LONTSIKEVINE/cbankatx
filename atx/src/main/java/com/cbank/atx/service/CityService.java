package com.cbank.atx.service;

import com.cbank.atx.domain.branch.City;
import com.cbank.atx.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    // Créer une ville
    public City create(City city) {
        // Règle métier : le code doit être unique
        if (cityRepository.existsByCode(city.getCode())) {
            throw new RuntimeException(
                    "Une ville avec le code "
                            + city.getCode()
                            + " existe déjà !"
            );
        }
        return cityRepository.save(city);
    }

    // Lister toutes les villes
    public List<City> getAll() {
        return cityRepository.findAll();
    }

    // Trouver une ville par ID
    public City getById(String id) {
        return cityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ville non trouvée : " + id));
    }

    // Modifier une ville
    public City update(String id, City newData) {
        City existing = getById(id);
        existing.setLabel(newData.getLabel());
        existing.setCode(newData.getCode());
        return cityRepository.save(existing);
    }

    // Supprimer une ville
    public void delete(String id) {
        // Vérifie que la ville existe avant de supprimer
        getById(id);
        cityRepository.deleteById(id);
    }
}
