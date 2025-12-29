package com.hb.cda.electricitybusiness.messaging;

import com.hb.cda.electricitybusiness.model.Booking;
import com.hb.cda.electricitybusiness.model.User;

public interface MailService {

    void sendVerificationCode(String toEmail, String code);

    void sendResetPassword(User user, String token);

    void sendReservationCreatedEmail(String ownerEmail, String stationName, String date, String start, String end);

    void sendReservationStatusUpdateEmail(String renterEmail, String status, String stationName, String message);

    void notifyOwnerPendingBooking(Booking booking);
    void notifyUserBookingConfirmed(Booking booking);
    void notifyUserBookingCancelled(Booking booking);
}
