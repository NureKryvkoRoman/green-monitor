package ua.nure.kryvko.greenmonitor.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.nure.kryvko.greenmonitor.user.User;
import ua.nure.kryvko.greenmonitor.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.nure.kryvko.greenmonitor.user.UserRole;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static class TokenPair {
        public String accessToken;
        public String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid AuthRequest authRequest) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(authRequest.getEmail());
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>(new AuthResponse("User not found"), HttpStatus.UNAUTHORIZED);
            }

            User user = optionalUser.get();
            TokenPair tokens = generateTokens(authRequest.getPassword(), user);
            AuthResponse loginResponse = new AuthResponse(
                    user.getId(),
                    tokens.accessToken,
                    tokens.refreshToken,
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthResponse("Invalid email, username, or password"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(new AuthResponse("An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private TokenPair generateTokens(String password, User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), // Always use email for authentication
                        password
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        return new TokenPair(accessToken, refreshToken);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody @Valid AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            return new ResponseEntity<>(new AuthResponse("User with this email already exists."), HttpStatus.CONFLICT);
        }

        User newUser = new User(
                authRequest.getEmail(),
                encoder.encode(authRequest.getPassword()),
                UserRole.USER
        );

        newUser = userRepository.save(newUser);
        TokenPair tokens = generateTokens(authRequest.getPassword(), newUser);
        AuthResponse signUpResponse = new AuthResponse(
                newUser.getId(),
                tokens.accessToken,
                tokens.refreshToken,
                newUser.getEmail(),
                newUser.getRole()
        );
        return new ResponseEntity<>(signUpResponse, HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtUtil.validateJwtToken(refreshToken) && "refresh".equals(jwtUtil.getTokenType(refreshToken))) {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("error", "Invalid refresh token"), HttpStatus.FORBIDDEN);
        }
    }
}