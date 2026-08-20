package com.rahul.SpringJWTSimpleDemo.service;

import com.rahul.SpringJWTSimpleDemo.dto.AuthResponse;
import com.rahul.SpringJWTSimpleDemo.dto.LoginRequest;
import com.rahul.SpringJWTSimpleDemo.dto.RegisterRequest;
import com.rahul.SpringJWTSimpleDemo.entity.Role;
import com.rahul.SpringJWTSimpleDemo.entity.User;
import com.rahul.SpringJWTSimpleDemo.exception.DuplicateUsernameException;
import com.rahul.SpringJWTSimpleDemo.exception.UserNotFoundException;
import com.rahul.SpringJWTSimpleDemo.repository.UserRepository;
import com.rahul.SpringJWTSimpleDemo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    //    public String register(RegisterRequest request) {
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new IllegalArgumentException("Username already taken");
//        }
//
//        Role role = Role.valueOf(request.getRole().toUpperCase());
//
//        User user = User.builder()
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(role)
//                .build();
//
//        userRepository.save(user);
//
//        return "User registered successfully with username: " + user.getUsername();
//    }
//
//    public AuthResponse login(LoginRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//        );
//
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
//        return new AuthResponse(token, user.getUsername(), user.getRole().name());
//    }
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException(request.getUsername());
        }

        Role role = Role.valueOf(request.getRole().toUpperCase());

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        return "User registered successfully with username: " + user.getUsername();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException(request.getUsername()));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}