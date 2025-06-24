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
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    static final String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            //TODO: remove checks as redundant since we already use @Valid??
            if (!Pattern.compile(EMAIL_PATTERN).matcher(loginRequest.getEmail()).matches()) {
                return new ResponseEntity<>(new AuthResponse("Invalid email address."), HttpStatus.BAD_REQUEST);
            }

            Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>(new AuthResponse("User not found"), HttpStatus.UNAUTHORIZED);
            }

            User user = optionalUser.get();
            Map<String, String> tokens = generateTokens(loginRequest.getPassword(), user);
            AuthResponse loginResponse = new AuthResponse(
                    user.getId(),
                    tokens.get("accessToken"),
                    tokens.get("refreshToken"),
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

    private Map<String, String> generateTokens(String password, User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), // Always use email for authentication
                        password
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody @Valid SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new AuthResponse("User with this email already exists."), HttpStatus.CONFLICT);
        }

        if (!Pattern.compile(EMAIL_PATTERN).matcher(signUpRequest.getEmail()).matches())
            return new ResponseEntity<>(new AuthResponse("Invalid email address."), HttpStatus.BAD_REQUEST);

        User newUser = new User(
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                UserRole.USER
        );

        newUser = userRepository.save(newUser);
        Map<String, String> tokens = generateTokens(signUpRequest.getPassword(), newUser);
        AuthResponse signUpResponse = new AuthResponse(
                newUser.getId(),
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
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