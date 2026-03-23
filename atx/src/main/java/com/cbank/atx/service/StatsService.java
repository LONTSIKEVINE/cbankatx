
package com.cbank.atx.service;

import com.cbank.atx.enums.RequestStatus;
import com.cbank.atx.repository.AtxRepository;
import com.cbank.atx.repository.BranchRepository;
import com.cbank.atx.repository.RequetsAtxRepository;
import com.cbank.atx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final RequetsAtxRepository
            requetsAtxRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final AtxRepository atxRepository;

    // ─────────────────────────────────────────
    // STATISTIQUES globales des demandes
    // → Retourne le nombre de demandes
    //   par statut
    // → Utilisé pour le tableau de bord
    //   principal du frontend
    // ─────────────────────────────────────────
    public Map<String, Object> getRequestStats() {

        Map<String, Object> stats =
                new HashMap<>();

        // Total de toutes les demandes
        long total = requetsAtxRepository.count();

        // Nombre par statut
        long pending = requetsAtxRepository
                .findByStatus(RequestStatus.PENDING)
                .size();

        long processing = requetsAtxRepository
                .findByStatus(
                        RequestStatus.PROCESSING)
                .size();

        long delivered = requetsAtxRepository
                .findByStatus(
                        RequestStatus.DELIVERED)
                .size();

        long ended = requetsAtxRepository
                .findByStatus(RequestStatus.ENDED)
                .size();

        // Remplit la Map avec les stats
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("processing", processing);
        stats.put("delivered", delivered);
        stats.put("ended", ended);

        return stats;
    }

    // ─────────────────────────────────────────
    // STATISTIQUES des demandes par agent
    // → Retourne les stats pour UN agent
    // → Utilisé pour voir la charge de travail
    //   de chaque agent
    // ─────────────────────────────────────────
    public Map<String, Object> getStatsByAgent(
            String userId) {

        Map<String, Object> stats =
                new HashMap<>();

        // Demandes assignées à cet agent
        long total = requetsAtxRepository
                .findByAssignedTo(userId)
                .size();

        // Demandes en cours pour cet agent
        long processing = requetsAtxRepository
                .findByAssignedToAndStatus(
                        userId,
                        RequestStatus.PROCESSING)
                .size();

        stats.put("userId", userId);
        stats.put("total", total);
        stats.put("processing", processing);

        return stats;
    }

    // ─────────────────────────────────────────
    // STATISTIQUES générales du système
    // → Retourne les compteurs globaux
    // → Utilisé pour le dashboard admin
    // ─────────────────────────────────────────
    public Map<String, Object> getGlobalStats() {

        Map<String, Object> stats =
                new HashMap<>();

        // Nombre total d'utilisateurs
        stats.put("totalUsers",
                userRepository.count());

        // Nombre d'utilisateurs actifs
        stats.put("activeUsers",
                userRepository
                        .findByActiveTrue().size());

        // Nombre total d'agences
        stats.put("totalBranches",
                branchRepository.count());

        // Nombre total d'attestations
        stats.put("totalAtxTypes",
                atxRepository.count());

        // Nombre total de demandes
        stats.put("totalRequests",
                requetsAtxRepository.count());

        // Ajoute les stats des demandes
        stats.putAll(getRequestStats());

        return stats;
    }
}
