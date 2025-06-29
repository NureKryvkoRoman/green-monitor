package ua.nure.kryvko.greenmonitor.auth;

import ua.nure.kryvko.greenmonitor.user.UserRole;

public class AuthResponse {
    String accessToken;
    String refreshToken;
    String email;
    UserRole role;
    String error;

    public AuthResponse(String error) {
        this.error = error;
    }

    public AuthResponse(String accessToken, String refreshToken, String email, UserRole role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
