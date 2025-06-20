package com.example.Login_Services.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailOtpService {

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private final long OTP_EXPIRY_MILLIS = 5 * 60 * 1000; // 5 mins

    public String sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Your OTP Code");
            helper.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
            mailSender.send(message);

            otpStore.put(toEmail, new OtpData(otp, System.currentTimeMillis()));
            return otp;
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public boolean verifyOtp(String email, String inputOtp) {
        OtpData data = otpStore.get(email);
        if (data == null) return false;

        boolean expired = System.currentTimeMillis() - data.timestamp > OTP_EXPIRY_MILLIS;
        return !expired && data.otp.equals(inputOtp);
    }
    public void clearOtp(String email) {
        otpStore.remove(email);
    }

    @Data
    private static class OtpData {
        private final String otp;
        private final long timestamp;
    }
}
