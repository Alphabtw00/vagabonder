package com.vagabonder.service;


import com.vagabonder.entity.User;
import com.vagabonder.repository.UserRepository;
import com.vagabonder.dto.LoginRequestDTO;
import com.vagabonder.dto.RegisterRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User registerUser(RegisterRequestDTO registerRequestDTO){
        if(userRepository.existsByEmailEquals(registerRequestDTO.getEmail())){
            throw new IllegalArgumentException("Email already in use");
        }
        User user = User.builder()
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .fullName(registerRequestDTO.getFullName())
                .build();
        return userRepository.save(user);
    }

    public User loginUser(LoginRequestDTO loginRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        return (User) authentication.getPrincipal();
    }
}
