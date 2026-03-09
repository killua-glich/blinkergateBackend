package com.blinkergate.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 6)
        private String password;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    public static class TokenResponse {
        private String token;
        private String username;
        private Integer lvl;
        private String role;
        private Integer currentXp;

        public TokenResponse(String token, String username, Integer lvl, String role, Integer currentXp) {
            this.token = token;
            this.username = username;
            this.lvl = lvl;
            this.role = role;
            this.currentXp = currentXp;
        }
    }
}
