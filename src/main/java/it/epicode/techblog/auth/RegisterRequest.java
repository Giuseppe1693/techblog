package it.epicode.techblog.auth;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String nome;
    private String cognome;
    private String città;
    private LocalDate dataDiNascita;
    private Role ruolo;
}
