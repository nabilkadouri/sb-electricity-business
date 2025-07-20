package com.hb.cda.electricitybusiness.service.util;

public interface EmailService {
    void sendVerificationCode(String toEmail, String code);

}
