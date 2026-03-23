package com.socialsync.socialsyncbackend.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendGridTwilioNotificationService implements NotificationService {

    private final String sendGridApiKey;
    private final String sendGridFromEmail;
    private final boolean smsEnabled;
    private final String twilioAccountSid;
    private final String twilioAuthToken;
    private final String twilioPhoneNumber;

    public SendGridTwilioNotificationService(
            @Value("${app.email.sendgrid.api-key:}") String sendGridApiKey,
            @Value("${app.email.from:no-reply@socialsync.local}") String sendGridFromEmail,
            @Value("${app.sms.enabled:false}") boolean smsEnabled,
            @Value("${app.sms.twilio.account-sid:}") String twilioAccountSid,
            @Value("${app.sms.twilio.auth-token:}") String twilioAuthToken,
            @Value("${app.sms.twilio.from-number:}") String twilioPhoneNumber) {
        this.sendGridApiKey = sendGridApiKey;
        this.sendGridFromEmail = sendGridFromEmail;
        this.smsEnabled = smsEnabled;
        this.twilioAccountSid = twilioAccountSid;
        this.twilioAuthToken = twilioAuthToken;
        this.twilioPhoneNumber = twilioPhoneNumber;
    }

    @PostConstruct
    void initTwilio() {
        if (smsEnabled && !twilioAccountSid.isBlank() && !twilioAuthToken.isBlank()) {
            Twilio.init(twilioAccountSid, twilioAuthToken);
        }
    }

    @Override
    public void sendVerificationEmail(String email, String verificationLink) {
        String subject = "Verify your SocialSync account";
        String body = "Click to verify your account: " + verificationLink;
        sendEmail(email, subject, body);
    }

    @Override
    public void sendMfaCodeEmail(String email, String code) {
        String subject = "Your SocialSync MFA code";
        String body = "Your verification code is: " + code + ". It expires in 5 minutes.";
        sendEmail(email, subject, body);
    }

    @Override
    public void sendMfaCodeSms(String phoneNumber, String code) {
        if (!smsEnabled || twilioAccountSid.isBlank() || twilioAuthToken.isBlank() || twilioPhoneNumber.isBlank()) {
            log.info("SMS disabled or Twilio not configured; skipping SMS for {}", phoneNumber);
            return;
        }

        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                "Your SocialSync MFA code is: " + code + ". It expires in 5 minutes."
        ).create();
    }

    private void sendEmail(String to, String subject, String body) {
        if (sendGridApiKey.isBlank()) {
            log.info("SendGrid API key not configured; skipping email to {}", to);
            return;
        }

        try {
            Email from = new Email(sendGridFromEmail);
            Email recipient = new Email(to);
            Content content = new Content("text/plain", body);
            Mail mail = new Mail(from, subject, recipient, content);

            SendGrid sendGrid = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("Failed to send email via SendGrid. Status: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to send email notification", ex);
        }
    }
}
