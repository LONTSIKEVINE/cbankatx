package com.cbank.atx.service;

import com.cbank.atx.domain.branch.City;
import com.cbank.atx.exception.BusinessException;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    // Repository qui parle à MongoDB
    // collection "cities"
    private final CityRepository cityRepository;

    // ─────────────────────────────────────────
    // CRÉER une ville
    // Règle métier : le code doit être unique
    // Ex: on ne peut pas avoir 2 villes
    //     avec le code "1"
    // ─────────────────────────────────────────
    public City create(City city) {

        // Vérifie si le code existe déjà
        // dans la collection "cities"
        if (cityRepository.existsByCode(
                city.getCode())) {

            // ❌ Code déjà utilisé
            // → BusinessException = erreur métier
            // → GlobalExceptionHandler retourne 400
            throw new BusinessException(
                    "Une ville avec le code "
                            + city.getCode()
                            + " existe déjà !");
        }

        // ✅ Code unique → sauvegarde dans MongoDB
        return cityRepository.save(city);
    }

    // ─────────────────────────────────────────
    // LIRE toutes les villes
    // → Retourne toute la collection "cities"
    // ─────────────────────────────────────────
    public List<City> getAll() {
        return cityRepository.findAll();
    }

    // ─────────────────────────────────────────
    // LIRE une ville par son ID MongoDB
    // → Si non trouvée → 404 automatique
    // ─────────────────────────────────────────
    public City getById(String id) {
        return cityRepository.findById(id)

                // orElseThrow → si le document
                // n'existe pas dans MongoDB
                // → lance l'exception
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ville non trouvée : " + id));
    }

    // ─────────────────────────────────────────
    // MODIFIER une ville
    // → Charge d'abord la ville existante
    // → Modifie les champs
    // → Sauvegarde dans MongoDB
    // ─────────────────────────────────────────
    public City update(String id, City newData) {

        // Charge la ville existante
        // (lance 404 si non trouvée)
        City existing = getById(id);

        // Met à jour les champs
        existing.setLabel(newData.getLabel());
        existing.setCode(newData.getCode());

        // Sauvegarde les modifications
        return cityRepository.save(existing);
    }

    // ─────────────────────────────────────────
    // SUPPRIMER une ville
    // → Vérifie d'abord qu'elle existe
    // → Puis supprime de MongoDB
    // ─────────────────────────────────────────
    public void delete(String id) {

        // Vérifie existence
        // (lance 404 si non trouvée)
        getById(id);

        // Supprime de MongoDB
        cityRepository.deleteById(id);
    }
}