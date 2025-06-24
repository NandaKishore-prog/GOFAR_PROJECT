package com.example.Login_Services.Controllers;

import com.example.Login_Services.Repository.MyAppUserRepository;
import com.example.Login_Services.Services.EmailOtpService;
import com.example.Login_Services.model.MyAppUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@Controller
public class ContentController {

    @Autowired
    private EmailOtpService emailOtpService;

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    private Optional<MyAppUser> getLoggedInUser() {
        if (!isAuthenticated()) return Optional.empty();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return myAppUserRepository.findByUsername(username);
    }

    @GetMapping("/login")
    public String loginPage() {
        return isAuthenticated() ? "redirect:/home" : "index";
    }

    @GetMapping("/signup")
    public String signuppage() {
        return isAuthenticated() ? "redirect:/home" : "signup";
    }

    @GetMapping("/home")
    public String dashboard() {
        Optional<MyAppUser> user = getLoggedInUser();
        user.ifPresentOrElse(
                u -> System.out.println("Welcome back, " + u.getEmail()),
                () -> System.out.println("Please login first")
        );
        return "home";
    }

    @GetMapping("/map")
    public String map() {
        Optional<MyAppUser> user = getLoggedInUser();
        user.ifPresentOrElse(
                u -> System.out.println("Welcome back, " + u.getEmail()),
                () -> System.out.println("Please login first")
        );
        return "map";
    }

    @GetMapping("/otp")
    public String otp() {
        return "otp";
    }

    @PostMapping("/otp")
    public void verifyOtp(
            @RequestParam String otp,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("otp_email");
        String username = (String) session.getAttribute("otp_username");
        String password = (String) session.getAttribute("otp_password");

        if (email != null && username != null && password != null && emailOtpService.verifyOtp(email, otp)) {
            MyAppUser user = new MyAppUser();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(password);
            myAppUserRepository.save(user);

            emailOtpService.clearOtp(email);
            session.invalidate(); // Clear everything

            response.sendRedirect("/login");
        } else {
            response.sendRedirect("/otp?error=invalid");
        }
    }
}
