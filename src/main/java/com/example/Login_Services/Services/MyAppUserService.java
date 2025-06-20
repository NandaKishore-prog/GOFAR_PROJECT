package com.example.Login_Services.Services;


import com.example.Login_Services.Repository.MyAppUserRepository;
import com.example.Login_Services.model.MyAppUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MyAppUserService implements UserDetailsService{

    @Autowired
    private MyAppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username received: " + username); // ← print input username

        Optional<MyAppUser> user = repository.findByUsername(username);
        if (user.isPresent()) {
            System.out.println("log11");
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .roles("USER") // Add default role

                    .build();
        }else{
            throw new UsernameNotFoundException(username);
        }
    }



}