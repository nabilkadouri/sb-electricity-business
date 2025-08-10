package com.hb.cda.electricitybusiness.messaging;

import com.hb.cda.electricitybusiness.model.User;

public interface MailService {

    void sendVerificationCode(String toEmail, String code);

    void sendResetPassword(User user, String token);
}
