package com.example.Login_Services.Controllers;

import com.example.Login_Services.Repository.MyAppUserRepository;
import com.example.Login_Services.Services.EmailOtpService;
import com.example.Login_Services.model.MyAppUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@RestController
public class RegistrationController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private EmailOtpService emailOtpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @PostMapping(value = "/api/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@Valid @RequestBody MyAppUser user, HttpSession session) {
        if (myAppUserRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body("USERNAME_EXISTS");
        }

        if (myAppUserRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("EMAIL_EXISTS");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String otp = String.valueOf(1000 + random.nextInt(9000));
        emailOtpService.sendOtpEmail(user.getEmail(), otp);

        session.setAttribute("otp_username", user.getUsername());
        session.setAttribute("otp_email", user.getEmail());
        session.setAttribute("otp_password", user.getPassword());

        return ResponseEntity.ok("OTP sent to your email.");
    }

}
