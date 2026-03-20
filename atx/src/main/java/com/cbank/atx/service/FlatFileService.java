

package com.cbank.atx.service;

import com.cbank.atx.domain.atx.Atx;
import com.cbank.atx.domain.atx.LocaleAtx;
import com.cbank.atx.domain.branch.Branch;
import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.domain.settings.Field;
import com.cbank.atx.domain.settings.FlatFileFormat;
import com.cbank.atx.domain.settings.Settings;
import com.cbank.atx.enums.DataSource;
import com.cbank.atx.repository.AtxRepository;
import com.cbank.atx.repository.BranchRepository;
import com.cbank.atx.repository.RequetsAtxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlatFileService {

    private final RequetsAtxRepository
            requetsAtxRepository;
    private final AtxRepository atxRepository;
    private final BranchRepository branchRepository;
    private final SettingsService settingsService;

    // ─────────────────────────────────────────
    // GÉNÉRER le fichier plat
    // pour une demande donnée
    // ─────────────────────────────────────────
    public String generateFlatFile(
            String requestId) {

        // Étape 1 : charger la demande
        RequetsAtx request = requetsAtxRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Demande non trouvée : "
                                        + requestId));

        // Étape 2 : charger l'attestation
        Atx atx = atxRepository
                .findById(request.getAtxId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Attestation non trouvée !"));

        // Étape 3 : charger les settings
        Settings settings = settingsService.get();
        FlatFileFormat format =
                settings.getFlatFileFormat();

        // Étape 4 : charger l'agence
        // (pour le compte taxes et produits)
        Branch branch = null;
        if (request.getCreatedBy() != null) {
            // On utilise l'agence de la demande
            branch = branchRepository
                    .findAll()
                    .stream()
                    .findFirst()
                    .orElse(null);
        }

        // Étape 5 : calculer le prix TTC
        LocaleAtx locale = atx.getLocales()
                .get("fr");
        double priceTTC = locale.getPrice();
        if (locale.isTaxable()) {
            priceTTC += locale.getPrice()
                    * locale.getTaxePercentage()
                    / 100.0;
        }

        // Étape 6 : construire les colonnes
        List<String> columns = new ArrayList<>();

        // Trier les fields par range
        List<Field> sortedFields = format
                .getFields()
                .stream()
                .sorted((a, b) ->
                        Integer.compare(
                                a.getRange(),
                                b.getRange()))
                .toList();

        for (Field field : sortedFields) {
            String value = getFieldValue(
                    field,
                    request,
                    priceTTC,
                    settings
            );

            // Si fillFieldLength → padding
            if (Boolean.TRUE.equals(
                    format.getFillFieldLength())) {
                value = padValue(
                        value,
                        field.getSize()
                );
            }

            columns.add(value);
        }

        // Étape 7 : assembler avec le délimiteur
        String delimiter = format.getDelimiter();
        if (delimiter == null
                || delimiter.isEmpty()) {
            delimiter = ";";
        }

        return String.join(delimiter, columns);
    }

    // ─────────────────────────────────────────
    // RÉCUPÉRER la valeur d'un champ
    // ─────────────────────────────────────────
    private String getFieldValue(
            Field field,
            RequetsAtx request,
            double priceTTC,
            Settings settings) {

        // Si valeur fixe définie
        if (field.getValue() != null
                && !field.getValue().isEmpty()) {
            return field.getValue();
        }

        // Si dataSource = MANUAL
        // → utiliser les données métier
        if (field.getDataSource()
                == DataSource.MANUAL) {

            String label = field.getLabel()
                    .toLowerCase();

            // Mapper selon le label du champ
            if (label.contains("agence")
                    || label.contains("branch")) {
                return settings
                        .getFlatFileBusinessData()
                        .getRowBranch();
            }
            if (label.contains("compte")
                    || label.contains("account")) {
                return request.getAccountNumber();
            }
            if (label.contains("montant")
                    || label.contains("amount")) {
                return String.valueOf(
                        (int) priceTTC);
            }
            if (label.contains("sens")
                    || label.contains("side")) {
                return settings
                        .getFlatFileBusinessData()
                        .getRowSide();
            }
            if (label.contains("agent")
                    || label.contains("user")) {
                return settings
                        .getFlatFileBusinessData()
                        .getRowUser();
            }
            if (label.contains("date")) {
                return settings
                        .getFlatFileBusinessData()
                        .getRowDate();
            }
        }

        return "";
    }

    // ─────────────────────────────────────────
    // PADDING — remplir jusqu'à la taille max
    // ─────────────────────────────────────────
    private String padValue(
            String value, int size) {

        if (value.length() >= size) {
            return value.substring(0, size);
        }

        StringBuilder sb =
                new StringBuilder(value);
        while (sb.length() < size) {
            sb.append(' ');
        }
        return sb.toString();
    }
}