package com.socialsync.socialsyncbackend.services;

public interface NotificationService {
    void sendVerificationEmail(String email, String verificationLink);

    void sendMfaCodeEmail(String email, String code);

    void sendMfaCodeSms(String phoneNumber, String code);
}
