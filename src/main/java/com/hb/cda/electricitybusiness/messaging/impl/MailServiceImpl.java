package com.hb.cda.electricitybusiness.messaging.impl;

import com.hb.cda.electricitybusiness.messaging.MailService;
import com.hb.cda.electricitybusiness.model.Booking;
import com.hb.cda.electricitybusiness.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSenderImpl mailSender;
    private JavaMailSender javaMailSender;

    public MailServiceImpl(JavaMailSender javaMailSender, JavaMailSenderImpl mailSender) {
        this.javaMailSender = javaMailSender;
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        //Construction du mail
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Votre code de vérification");
        mailMessage.setText("Code de vérification : " + code);
        mailMessage.setFrom("no-reply@electricity-business.com");

        try {
            //Envoie du mail
            javaMailSender.send(mailMessage);
            System.out.println("E-mail de vérification envoyé à : " + toEmail + "avec le code: " + code);
        } catch (MailException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail de vérification à " + toEmail + " : " + e.getMessage());
        }
    }

    @Override
    public void sendResetPassword(User user, String token) {
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String message = """
                    To reset your password click on <a href="%s">this link</a>
                    """
                .formatted(serverUrl+"/reset-password.html?token="+token);
        sendMailBase(user.getEmail(), message, "Spring Holiday Reset Password");

    }

    @Override
    public void sendReservationCreatedEmail(String ownerEmail, String stationName, String date, String start, String end) {
        String subject = "Nouvelle réservation sur votre borne";
        String message = """
            Bonjour,

            Une nouvelle réservation a été effectuée sur votre borne « %s ».

            Date : %s
            Heure : %s - %s

            Vous pouvez accepter ou annuler cette réservation depuis votre espace propriétaire.

            L'équipe Electricity Business
            """.formatted(stationName, date, start, end);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(ownerEmail);
        mail.setSubject(subject);
        mail.setText(message);
        mail.setFrom("no-reply@electricity-business.com");

        javaMailSender.send(mail);
    }

    @Override
    public void sendReservationStatusUpdateEmail(String renterEmail, String status, String stationName, String messageText) {
        String subject = "Mise à jour de votre réservation";

        String emailBody = """
            Bonjour,
            
            Le propriétaire a mis à jour votre réservation concernant la borne : %s.
            
            Nouveau statut : %s
            
            %s
            
            L'équipe Electricity Business
            """.formatted(
                stationName,
                status,
                (messageText == null || messageText.isEmpty())
                        ? ""
                        : "Motif : " + messageText
        );

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(renterEmail);
        mail.setSubject(subject);
        mail.setText(emailBody);
        mail.setFrom("no-reply@electricity-business.com");

        javaMailSender.send(mail);
    }


    @Override
    public void notifyOwnerPendingBooking(Booking booking) {

        String ownerEmail = booking.getChargingStation().getUser().getEmail();
        String stationName = booking.getChargingStation().getNameStation();

        sendReservationCreatedEmail(
                ownerEmail,
                stationName,
                booking.getStartDate().toLocalDate().toString(),
                booking.getStartDate().toLocalTime().toString(),
                booking.getEndDate().toLocalTime().toString()
        );
    }


    @Override
    public void notifyUserBookingConfirmed(Booking booking) {

        String renterEmail = booking.getUser().getEmail();
        String stationName = booking.getChargingStation().getNameStation();

        sendReservationStatusUpdateEmail(renterEmail, "Confirmée", stationName, "Votre réservation a été confirmée !"
        );
    }


    @Override
    public void notifyUserBookingCancelled(Booking booking) {

        String renterEmail = booking.getUser().getEmail();
        String stationName = booking.getChargingStation().getNameStation();

        String reason = booking.getCancelReason() != null ? booking.getCancelReason(): "Aucun motif fourni.";

        sendReservationStatusUpdateEmail(renterEmail, "Annulée", stationName, "Votre réservation a été annulée. Motif : " + reason
        );
    }


    private void sendMailBase(String to, String message, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(to);
            helper.setFrom("no-reply@electricity-business.com");
            helper.setSubject(subject);

            helper.setText(message,true);
            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Unable to send mail", e);
        }
    }
}
