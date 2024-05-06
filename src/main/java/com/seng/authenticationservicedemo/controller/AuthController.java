package com.seng.authenticationservicedemo.controller;


import com.seng.authenticationservicedemo.entity.Auth;
import com.seng.authenticationservicedemo.jwt.JwtTokenProvider;
import com.seng.authenticationservicedemo.jwt.TokenBlacklistService;
import com.seng.authenticationservicedemo.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthRepository authRepository;

    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider jwtTokenProvider;


    private AuthenticationManager authenticationManager;

    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthController(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, TokenBlacklistService tokenBlacklistService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Auth auth) {
        auth.setPassword(null);
        auth.setEmailVerified(false);
        auth.setPhoneVerified(false);
        Auth result = authRepository.save(auth);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Auth loginDetails) {
        Auth auth = authRepository.findByEmail(loginDetails.getEmail()).orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(loginDetails.getPassword(), auth.getPassword())) {
            throw new RuntimeException("Incorrect password.");
        }

        String token = jwtTokenProvider.generateToken(auth.getEmail(), auth.getUserId());
        return ResponseEntity.ok(token);
    }


    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (jwtTokenProvider.validateToken(token)) {
            String userEmail = jwtTokenProvider.getEmailFromJWT(token);
            Long userId = jwtTokenProvider.getUserIdFromJWT(token);
            Auth auth = authRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));

            // Password hash is not included in the response
            auth.setPassword(null);
            return ResponseEntity.ok(auth);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (jwtTokenProvider.validateToken(token)) {
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or Expired Token");
        }
    }

}