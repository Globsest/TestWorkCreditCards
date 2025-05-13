package com.globsest.testworkcreditcards.controller;


import com.globsest.testworkcreditcards.dto.AuthResponse;
import com.globsest.testworkcreditcards.dto.LoginRequest;
import com.globsest.testworkcreditcards.dto.RefreshRequest;
import com.globsest.testworkcreditcards.dto.RegisterRequest;
import com.globsest.testworkcreditcards.entity.User;
import com.globsest.testworkcreditcards.repository.UserRepository;
import com.globsest.testworkcreditcards.service.UserService;
import com.globsest.testworkcreditcards.token.JWTCore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает нового пользователя с указанной ролью",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная регистрация"),
                    @ApiResponse(responseCode = "400", description = "Email уже существует")
            }
    )
    ResponseEntity<?> register (@RequestBody RegisterRequest registerRequest) throws AuthException {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AuthException("Email already exists");
        }

        try {
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
        } catch (Exception e) {
            throw new AuthException("Registration failed: " + e.getMessage());
        }

    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Пользователь заходит в систему",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный вход"),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws AuthException {
        try {
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
        } catch (BadCredentialsException e) {
            throw new AuthException("Invalid email or password");
        } catch (Exception e) {
            throw new AuthException("Login failed: " + e.getMessage());
        }

    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Обновление токена",
            description = "Генерирует новую пару access/refresh токенов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Токены обновлены"),
                    @ApiResponse(responseCode = "400", description = "Невалидный refresh-токен")
            }
    )
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) throws AuthException {
        if (!jwtCore.validateToken(refreshRequest.getRefreshToken(), true)) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }
        try {
            String passport = jwtCore.getUsernameFromToken(refreshRequest.getRefreshToken(), true);
            UserDetails userDetails = userService.loadUserByUsername(passport);

            String newAccessToken = jwtCore.generateAccessToken(userDetails);
            String newRefreshToken = jwtCore.generateRefreshToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(
                    newAccessToken,
                    newRefreshToken
            ));
        } catch (Exception e) {
            throw new AuthException("Token refresh failed: " + e.getMessage());
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }

}
