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

            if (notifyBO) {
                sendEmail(
                        agentEmail,
                        "CBank ATX - Nouvelle demande assignée",
                        "Bonjour,\n\n"
                                + "Une nouvelle demande vous a été assignée.\n"
                                + "ID Demande : " + requestId
                                + "\n\nCordialement,\nCBank ATX"
                );
            }

        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Demande livrée
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

            if (notifyCustomer && clientEmail != null) {
                sendEmail(
                        clientEmail,
                        "CBank ATX - Attestation disponible",
                        "Bonjour,\n\n"
                                + "Votre attestation est prête.\n"
                                + "ID Demande : " + requestId
                                + "\n\nCordialement,\nCBank ATX"
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
                                + "Cordialement,\nCBank ATX"
                );
            }

        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Invitation utilisateur (UPDATED 🔥)
    // ─────────────────────────────────────────
    public void notifyUserInvitation(
            String email,
            String firstname,
            String lastname,
            String password) {

        try {

            boolean notifyBO = settingsService
                    .get()
                    .getNotifications()
                    .getUserInvitation()
                    .getBackOffice();

            if (notifyBO) {

                sendEmail(
                        email,
                        "CBank ATX - Bienvenue !",
                        "Bonjour " + firstname + " " + lastname + ",\n\n"
                                + "Votre compte CBank ATX a été créé avec succès.\n\n"
                                + "Vos identifiants :\n"
                                + "Email       : " + email + "\n"
                                + "Mot de passe: " + password + "\n\n"
                                + "⚠️ Pensez à changer votre mot de passe après votre première connexion.\n\n"
                                + "Connectez-vous sur :\n"
                                + "http://cbank-atx.cm\n\n"
                                + "Cordialement,\nCBank ATX"
                );

                System.out.println(
                        "✅ Email invitation envoyé à : " + email);
            }

        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // NOTIFIER — Transfert CFT échoué
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
                                + "Le transfert CFT a échoué pour la demande :\n"
                                + "ID : " + requestId
                                + "\n\nVeuillez vérifier la connexion CFT.\n\n"
                                + "Cordialement,\nCBank ATX"
                );
            }

        } catch (Exception e) {
            System.out.println(
                    "⚠️ Notification non envoyée : "
                            + e.getMessage());
        }
    }
}