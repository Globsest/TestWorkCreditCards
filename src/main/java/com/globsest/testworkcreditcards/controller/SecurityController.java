package com.globsest.testworkcreditcards.controller;


import com.globsest.testworkcreditcards.dto.AuthResponse;
import com.globsest.testworkcreditcards.dto.LoginRequest;
import com.globsest.testworkcreditcards.dto.RefreshRequest;
import com.globsest.testworkcreditcards.dto.RegisterRequest;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.repository.UserRepository;
import com.globsest.testworkcreditcards.service.UserService;
import com.globsest.testworkcreditcards.token.JWTCore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SecurityController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JWTCore jwtCore;
    private UserService userService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtCore(JWTCore jwtCore) {
        this.jwtCore = jwtCore;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    ResponseEntity<?> register (@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.FOUND).build();
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword_hash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setMiddleName(registerRequest.getMiddleName());
        user.setRole(registerRequest.getRole());
        user.setActive(true);

        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtCore.generateAccessToken(userDetails);
        String refreshToken = jwtCore.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        if (!jwtCore.validateToken(refreshRequest.getRefreshToken(), true)) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }

        String passport = jwtCore.getUsernameFromToken(refreshRequest.getRefreshToken(), true);
        UserDetails userDetails = userService.loadUserByUsername(passport);

        String newAccessToken = jwtCore.generateAccessToken(userDetails);
        String newRefreshToken = jwtCore.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                newRefreshToken
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }

}
