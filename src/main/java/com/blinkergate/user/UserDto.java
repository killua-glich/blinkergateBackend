package com.blinkergate.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class UserDto {

    @Data
    public static class UpdateRequest {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        private String role;

        private Integer currentXp;
    }

    @Data
    public static class Response {
        private Long id;
        private String username;
        private String email;
        private Integer lvl;
        private String role;
        private Integer currentXp;

        public static Response from(User user) {
            Response r = new Response();
            r.id = user.getId();
            r.username = user.getUsername();
            r.email = user.getEmail();
            r.lvl = user.getLvl();
            r.role = user.getRole();
            r.currentXp = user.getCurrentXp();
            return r;
        }
    }
}
