package it.epicode.techblog.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
