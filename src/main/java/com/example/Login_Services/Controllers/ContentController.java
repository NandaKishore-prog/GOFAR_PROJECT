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
    Authentication auth;
    @Autowired
    private MyAppUserRepository myAppUserRepository;
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "index";
    }


    @GetMapping("/signup")
    public String signuppage(HttpServletRequest request) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "signup";
    }


    @GetMapping("/home")
    public String dashboard(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//user tracking made by nandhu
        String username = auth.getName(); //  username user tracking made by nandhu
        System.out.println(username);
        Optional<MyAppUser> user=myAppUserRepository.findByUsername(username);
        if (user != null) {
            System.out.println( ("Welcome back, " + user.get().getEmail()));
        } else {
            System.out.println( "Please login first");
        }
        return "home";
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
