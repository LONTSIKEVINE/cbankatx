package com.cbank.atx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SettingsService settingsService;

    // ─────────────────────────────────────────
    // ENVOYER un email simple
    // ─────────────────────────────────────────
    private void sendEmail(
            String to,
            String subject,
            String body) {
        try {
            SimpleMailMessage message =
                    new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println(
                    "✅ Email envoyé à : " + to);
        } catch (Exception e) {
            System.out.println(
                    "⚠️ Erreur envoi email : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Demande assignée
    // → Appelé quand BO assigne une demande
    // → Notifie l'agent et/ou le client
    //   selon la config dans Settings
    // ─────────────────────────────────────────
    public void notifyRequestAssignment(
            String agentEmail,
            String requestId) {

        try {
            boolean notifyBO = settingsService
                    .get()
                    .getNotifications()
                    .getRequestAssignment()
                    .getBackOffice();

            // Notifie l'agent si configuré
            if (notifyBO) {
                sendEmail(
                        agentEmail,
                        "CBank ATX - Nouvelle demande assignée",
                        "Bonjour,\n\n"
                                + "Une nouvelle demande vous "
                                + "a été assignée.\n"
                                + "ID Demande : " + requestId
                                + "\n\nCordialement,\n"
                                + "CBank ATX"
                );
            }
        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Demande traitée
    // → Appelé quand la demande est livrée
    // → Notifie le client et/ou le BO
    // ─────────────────────────────────────────
    public void notifyRequestDelivered(
            String clientEmail,
            String requestId) {

        try {
            boolean notifyCustomer =
                    settingsService.get()
                            .getNotifications()
                            .getRequestEndProcessing()
                            .getCustomer();

            // Notifie le client si configuré
            if (notifyCustomer
                    && clientEmail != null) {
                sendEmail(
                        clientEmail,
                        "CBank ATX - Attestation disponible",
                        "Bonjour,\n\n"
                                + "Votre attestation est prête.\n"
                                + "ID Demande : " + requestId
                                + "\n\nCordialement,\n"
                                + "CBank ATX"
                );
            }
        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Compte désactivé
    // → Appelé quand un compte est désactivé
    // ─────────────────────────────────────────
    public void notifyAccountDeactivated(
            String userEmail) {

        try {
            boolean notifyBO = settingsService
                    .get()
                    .getNotifications()
                    .getUserAccountDesactivate()
                    .getBackOffice();

            if (notifyBO) {
                sendEmail(
                        userEmail,
                        "CBank ATX - Compte désactivé",
                        "Bonjour,\n\n"
                                + "Votre compte a été désactivé.\n"
                                + "Contactez votre administrateur.\n\n"
                                + "Cordialement,\n"
                                + "CBank ATX"
                );
            }
        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Invitation utilisateur
    // → Appelé quand un nouveau user est créé
    // → Envoie ses credentials par email
    // ─────────────────────────────────────────
    public void notifyUserInvitation(
            String userEmail,
            String password) {

        try {
            boolean notifyBO = settingsService
                    .get()
                    .getNotifications()
                    .getUserInvitation()
                    .getBackOffice();

            if (notifyBO) {
                sendEmail(
                        userEmail,
                        "CBank ATX - Invitation",
                        "Bonjour,\n\n"
                                + "Votre compte CBank ATX "
                                + "a été créé.\n\n"
                                + "Email    : " + userEmail
                                + "\nMot de passe : " + password
                                + "\n\nConnectez-vous sur :\n"
                                + "http://cbank-atx.cm\n\n"
                                + "Cordialement,\n"
                                + "CBank ATX"
                );
            }
        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Transfert CFT échoué
    // → Appelé quand le transfert CFT échoue
    // ─────────────────────────────────────────
    public void notifyCftTransferFailed(
            String adminEmail,
            String requestId) {

        try {
            boolean notifyBO = settingsService
                    .get()
                    .getNotifications()
                    .getFlatFileTransferFailed()
                    .getBackOffice();

            if (notifyBO) {
                sendEmail(
                        adminEmail,
                        "CBank ATX - ⚠️ Transfert CFT échoué",
                        "Bonjour,\n\n"
                                + "Le transfert CFT a échoué "
                                + "pour la demande :\n"
                                + "ID : " + requestId
                                + "\n\nVeuillez vérifier "
                                + "la connexion CFT.\n\n"
                                + "Cordialement,\n"
                                + "CBank ATX"
                );
            }
        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }
}