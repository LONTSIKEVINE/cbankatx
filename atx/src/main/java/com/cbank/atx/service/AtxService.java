package com.cbank.atx.service;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.atx.Param;
import com.cbank.atx.enums.DataSource;
import com.cbank.atx.exception.BusinessException;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.AtxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AtxService {

    // Repository qui parle à MongoDB
    // collection "atxs"
    private final AtxRepository atxRepository;

    // ─────────────────────────────────────────
    // CRÉER un type d'attestation
    // Règle métier : le code doit être unique
    // ─────────────────────────────────────────
    public Atx create(Atx atx) {

        // Vérifie si le code existe
        // dans la version française
        if (atx.getLocales().containsKey("fr")) {
            String code = atx.getLocales()
                    .get("fr").getAtxCode();

            if (atxRepository
                    .findByAtxCode(code)
                    .isPresent()) {

                // ❌ Code déjà utilisé → 400
                throw new BusinessException(
                        "Code attestation déjà "
                                + "utilisé : " + code);
            }
        }

        // ✅ Code unique → sauvegarde
        return atxRepository.save(atx);
    }

    // ─────────────────────────────────────────
    // LIRE toutes les attestations
    // ─────────────────────────────────────────
    public List<Atx> getAll() {
        return atxRepository.findAll();
    }

    // ─────────────────────────────────────────
    // LIRE une attestation par ID
    // ─────────────────────────────────────────
    public Atx getById(String id) {
        return atxRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attestation non trouvée : "
                                        + id));
    }

    // ─────────────────────────────────────────
    // LIRE la version localisée
    // → Cherche d'abord dans la langue demandée
    // → Si non trouvée → retourne le français
    // ─────────────────────────────────────────
    public LocaleAtx getLocale(
            String atxId, String lang) {

        Atx atx = getById(atxId);

        // Cherche la version dans la langue
        LocaleAtx locale =
                atx.getLocales().get(lang);

        // Si non trouvée → fallback français
        if (locale == null) {
            locale = atx.getLocales().get("fr");
        }

        // Si même le français n'existe pas
        if (locale == null) {
            throw new ResourceNotFoundException(
                    "Aucune version disponible "
                            + "pour : " + lang);
        }

        return locale;
    }

    // ─────────────────────────────────────────
    // CALCULER le prix TTC
    // Formule : TTC = HT + (HT × taxe / 100)
    // Ex: 2500 + (2500 × 19 / 100) = 2975 FCFA
    // ─────────────────────────────────────────
    public double calculateTTC(
            String atxId, String lang) {

        LocaleAtx locale = getLocale(atxId, lang);

        // Si pas taxable → prix HT = prix TTC
        if (!locale.isTaxable()) {
            return locale.getPrice();
        }

        // Calcul de la taxe
        double taxe = locale.getPrice()
                * locale.getTaxePercentage()
                / 100.0;

        // Prix TTC = HT + taxe
        return locale.getPrice() + taxe;
    }

    // ─────────────────────────────────────────
    // VÉRIFIER si une agence peut délivrer
    // → Vérifie si l'agence est dans la liste
    //   des agences habilitées
    // ─────────────────────────────────────────
    public boolean canBranchDeliver(
            String atxId, String lang,
            String branchId) {

        LocaleAtx locale = getLocale(atxId, lang);

        return locale.getDeliverableInBranchs()
                .stream()
                .anyMatch(d -> d.getBranchId()
                        .equals(branchId));
    }

    // ─────────────────────────────────────────
    // RÉCUPÉRER les params manuels
    // → Retourne seulement les params
    //   que le BO doit saisir manuellement
    // → Utilisé par React pour afficher
    //   le formulaire de saisie
    // ─────────────────────────────────────────
    public List<Param> getManualParams(
            String atxId, String lang) {

        LocaleAtx locale = getLocale(atxId, lang);

        // Filtre uniquement les params MANUAL
        return locale.getParams().stream()
                .filter(p -> p.getDataSource()
                        == DataSource.MANUAL)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // MODIFIER une attestation
    // ─────────────────────────────────────────
    public Atx update(String id, Atx newData) {
        Atx existing = getById(id);
        existing.setLocales(newData.getLocales());
        return atxRepository.save(existing);
    }

    // ─────────────────────────────────────────
    // SUPPRIMER une attestation
    // ─────────────────────────────────────────
    public void delete(String id) {
        getById(id);
        atxRepository.deleteById(id);
    }
}