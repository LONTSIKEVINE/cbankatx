package com.cbank.atx.service;

import com.cbank.atx.domain.branch.Branch;
import com.cbank.atx.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    // Créer une agence
    public Branch create(Branch branch) {
        // Règle métier : le code agence doit être unique
        if (branchRepository.existsByCode(branch.getCode())) {
            throw new RuntimeException(
                    "Une agence avec le code "
                            + branch.getCode()
                            + " existe déjà !"
            );
        }
        return branchRepository.save(branch);
    }

    // Lister toutes les agences
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    // Trouver une agence par ID
    public Branch getById(String id) {
        return branchRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Agence non trouvée : " + id));
    }

    // Lister les agences d'une ville
    public List<Branch> getByCity(String cityId) {
        return branchRepository.findByCityId(cityId);
    }

    // Modifier une agence
    public Branch update(String id, Branch newData) {
        Branch existing = getById(id);
        existing.setLabel(newData.getLabel());
        existing.setCode(newData.getCode());
        existing.setTaxesAccount(newData.getTaxesAccount());
        existing.setProductsAccount(newData.getProductsAccount());
        existing.setManagerId(newData.getManagerId());
        existing.setCityId(newData.getCityId());
        return branchRepository.save(existing);
    }

    // Supprimer une agence
    public void delete(String id) {
        getById(id);
        branchRepository.deleteById(id);
    }
}