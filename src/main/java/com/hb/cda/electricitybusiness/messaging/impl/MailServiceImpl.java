package com.hb.cda.electricitybusiness.messaging.impl;

import com.hb.cda.electricitybusiness.messaging.MailService;
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

    private void sendMailBase(String to, String message, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(to);
            helper.setFrom("no-reply@electricity-business.com");
            helper.setSubject(subject);

            helper.setText(message,true); //Temporaire, email à remplacer par un JWT
            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Unable to send mail", e);
        }
    }



}
