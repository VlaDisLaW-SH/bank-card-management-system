package com.card_management.controllers;

import com.card_management.technical.util.JwtUtil;
import com.card_management.users_api.dto.AuthRequest;
import com.card_management.users_api.dto.UserCreateDto;
import com.card_management.users_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserCreateDto userDto) {
        var user = userService.create(userDto);
        return ResponseEntity.ok("Пользователь с Email " + user.getEmail() + " зарегистрирован.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        return ResponseEntity.ok(token);
    }
}
