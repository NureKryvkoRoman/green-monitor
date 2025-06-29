package ua.nure.kryvko.greenmonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.nure.kryvko.greenmonitor.auth.*;
import ua.nure.kryvko.greenmonitor.config.WebSecurityConfig;
import ua.nure.kryvko.greenmonitor.user.User;
import ua.nure.kryvko.greenmonitor.user.UserRepository;
import ua.nure.kryvko.greenmonitor.user.UserRole;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({AuthControllerTest.TestConfig.class, WebSecurityConfig.class})
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    static class TestConfig {
        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(userRepository);
    }

    @Test
    void signup_shouldAcceptValidEmail() throws Exception {
        User expectedUser = new User("user@mail.com", "encodedPwd", UserRole.USER);
        CustomUserDetails userDetails = new CustomUserDetails(expectedUser);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthRequest loginData = new AuthRequest("user@mail.com", "pwd");

        when(userRepository.save(any(User.class)))
                .thenReturn(expectedUser);
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        when(jwtUtil.generateAccessToken(any())).thenReturn("mockAccessToken");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("mockRefreshToken");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginData))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void signup_shouldRejectInvalidEmail() throws Exception {
        User expectedUser = new User("user@mail.com", "encodedPwd", UserRole.USER);
        CustomUserDetails userDetails = new CustomUserDetails(expectedUser);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthRequest loginData = new AuthRequest("usermail.com", "pwd");

        when(userRepository.save(any(User.class)))
                .thenReturn(expectedUser);
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        when(jwtUtil.generateAccessToken(any())).thenReturn("mockAccessToken");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("mockRefreshToken");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginData))
                )
                .andExpect(status().isBadRequest());
    }
}
