package com.cbank.atx.service;

import com.cbank.atx.domain.request.RequetsAtx;
import com.cbank.atx.domain.user.UserAssignment;
import com.cbank.atx.enums.RequestStatus;
import com.cbank.atx.repository.RequetsAtxRepository;
import com.cbank.atx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequetsAtxService {
    private final RequetsAtxRepository requetsAtxRepository;
    private final UserRepository userRepository;

    public RequetsAtx create(RequetsAtx requetsAtx) {
        requetsAtx.setStatus(RequestStatus.PENDING);
        requetsAtx.setRequestedAt(new Date());
        return requetsAtxRepository.save(requetsAtx);
    }

    public List<RequetsAtx> getAll() {
        return requetsAtxRepository.findAll();
    }

    public RequetsAtx getById(String id) {
        return requetsAtxRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Demande non trouvée : " + id));
    }

    public List<RequetsAtx> getByStatus(RequestStatus status) {
        return requetsAtxRepository.findByStatus(status);
    }

    public List<RequetsAtx> getByAgent(String userId) {
        return requetsAtxRepository.findByAssignedTo(userId);
    }

    public RequetsAtx assign(String requestId, String userId) {
        RequetsAtx request = getById(requestId);
        userRepository.findById(userId)
                .filter(u -> u.getActive())
                .orElseThrow(() ->
                        new RuntimeException("Agent non trouvé ou inactif : " + userId));
        UserAssignment assignment = new UserAssignment();
        assignment.setUserId(userId);
        assignment.setStatus(RequestStatus.PROCESSING);
        assignment.setAssignedAt(new Date());
        request.getAssignments().add(assignment);
        request.setAssignedTo(userId);
        request.setStatus(RequestStatus.PROCESSING);
        return requetsAtxRepository.save(request);
    }

    public RequetsAtx deliver(String requestId) {
        RequetsAtx request = getById(requestId);
        if (request.getStatus() != RequestStatus.PROCESSING) {
            throw new RuntimeException(
                    "La demande n'est pas en cours de traitement !");
        }
        request.setStatus(RequestStatus.DELIVERED);
        request.setDeliveredAt(new Date());
        request.getAssignments().stream()
                .filter(a -> a.getEndedAt() == null)
                .findFirst()
                .ifPresent(a -> a.setEndedAt(new Date()));
        return requetsAtxRepository.save(request);
    }

    public RequetsAtx close(String requestId) {
        RequetsAtx request = getById(requestId);
        if (request.getStatus() != RequestStatus.DELIVERED) {
            throw new RuntimeException(
                    "La demande n'a pas encore été livrée !");
        }
        request.setStatus(RequestStatus.ENDED);
        return requetsAtxRepository.save(request);
    }
}