package com.cbank.atx.service;

import com.cbank.atx.domain.branch.Branch;
import com.cbank.atx.exception.BusinessException;
import com.cbank.atx.exception.ResourceNotFoundException;
import com.cbank.atx.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    // Repository qui parle à MongoDB
    // collection "branchs"
    private final BranchRepository
            branchRepository;

    // ─────────────────────────────────────────
    // CRÉER une agence
    // Règle métier : le code doit être unique
    // Ex: on ne peut pas avoir 2 agences
    //     avec le code "AG-001"
    // ─────────────────────────────────────────
    public Branch create(Branch branch) {

        // Vérifie si le code agence
        // existe déjà dans MongoDB
        if (branchRepository.existsByCode(
                branch.getCode())) {

            // ❌ Code déjà utilisé
            // → erreur métier → 400
            throw new BusinessException(
                    "Une agence avec le code "
                            + branch.getCode()
                            + " existe déjà !");
        }

        // ✅ Code unique → sauvegarde
        return branchRepository.save(branch);
    }

    // ─────────────────────────────────────────
    // LIRE toutes les agences
    // ─────────────────────────────────────────
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    // ─────────────────────────────────────────
    // LIRE une agence par ID
    // → Si non trouvée → 404
    // ─────────────────────────────────────────
    public Branch getById(String id) {
        return branchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agence non trouvée : "
                                        + id));
    }

    // ─────────────────────────────────────────
    // LIRE les agences d'une ville
    // → Filtre par cityId
    // ─────────────────────────────────────────
    public List<Branch> getByCity(
            String cityId) {
        return branchRepository
                .findByCityId(cityId);
    }

    // ─────────────────────────────────────────
    // MODIFIER une agence
    // ─────────────────────────────────────────
    public Branch update(
            String id, Branch newData) {
        Branch existing = getById(id);
        existing.setLabel(newData.getLabel());
        existing.setCode(newData.getCode());
        existing.setTaxesAccount(
                newData.getTaxesAccount());
        existing.setProductsAccount(
                newData.getProductsAccount());
        existing.setManagerId(
                newData.getManagerId());
        existing.setCityId(newData.getCityId());
        return branchRepository.save(existing);
    }

    // ─────────────────────────────────────────
    // SUPPRIMER une agence
    // ─────────────────────────────────────────
    public void delete(String id) {
        getById(id);
        branchRepository.deleteById(id);
    }
}