package org.example.doantn.Controller;

import org.example.doantn.Dto.request.AuthRequest;
import org.example.doantn.Dto.request.RegisterRequest;
import org.example.doantn.Entity.User;
import org.example.doantn.Repository.UserRepo;
import org.example.doantn.Security.JwtUtil;
import org.example.doantn.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepo userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, UserRepo userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getRole(),
                request.getMssv(),
                request.getMaGv(),
                request.getUserType()
        );

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("token", token);
        response.put("role", user.getRole().getName());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().getName());

        return ResponseEntity.ok(response);
    }
}
