package com.cbank.atx.service;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.atx.Param;
import com.cbank.atx.enums.DataSource;
import com.cbank.atx.repository.AtxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AtxService {
    private final AtxRepository atxRepository;

    public Atx create(Atx atx) {
        if (atx.getLocales().containsKey("fr")) {
            String code = atx.getLocales().get("fr").getAtxCode();
            if (atxRepository.findByAtxCode(code).isPresent()) {
                throw new RuntimeException(
                        "Code attestation déjà utilisé : " + code);
            }
        }
        return atxRepository.save(atx);
    }

    public List<Atx> getAll() {
        return atxRepository.findAll();
    }

    public Atx getById(String id) {
        return atxRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Attestation non trouvée : " + id));
    }

    public LocaleAtx getLocale(String atxId, String lang) {
        Atx atx = getById(atxId);
        LocaleAtx locale = atx.getLocales().get(lang);
        if (locale == null) locale = atx.getLocales().get("fr");
        if (locale == null) throw new RuntimeException(
                "Aucune version disponible pour : " + lang);
        return locale;
    }

    public double calculateTTC(String atxId, String lang) {
        LocaleAtx locale = getLocale(atxId, lang);
        if (!locale.isTaxable()) return locale.getPrice();
        double taxe = locale.getPrice()
                * locale.getTaxePercentage() / 100.0;
        return locale.getPrice() + taxe;
    }

    public boolean canBranchDeliver(
            String atxId, String lang, String branchId) {
        LocaleAtx locale = getLocale(atxId, lang);
        return locale.getDeliverableInBranchs().stream()
                .anyMatch(d -> d.getBranchId().equals(branchId));
    }

    public List<Param> getManualParams(String atxId, String lang) {
        LocaleAtx locale = getLocale(atxId, lang);
        return locale.getParams().stream()
                .filter(p -> p.getDataSource() == DataSource.MANUAL)
                .collect(Collectors.toList());
    }

    public Atx update(String id, Atx newData) {
        Atx existing = getById(id);
        existing.setLocales(newData.getLocales());
        return atxRepository.save(existing);
    }

    public void delete(String id) {
        getById(id);
        atxRepository.deleteById(id);
    }
}