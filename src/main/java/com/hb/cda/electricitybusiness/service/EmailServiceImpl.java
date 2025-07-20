package com.hb.cda.electricitybusiness.service;

import com.hb.cda.electricitybusiness.service.util.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
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
}
