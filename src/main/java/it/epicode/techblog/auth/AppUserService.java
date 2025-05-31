package it.epicode.techblog.auth;

import it.epicode.techblog.users.User;
import it.epicode.techblog.users.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AppUser registerUser(String username, String password, String email, String nome,
                                String cognome, String città, LocalDate dataDiNascita) {

        if (appUserRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        Set<Role> roles = new HashSet<>();
        if (appUserRepository.count() == 0) {
            roles.add(Role.ROLE_ADMIN); // 👑 Primo utente
        } else {
            roles.add(Role.ROLE_USER); // 👤 Default
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRoles(roles);
        appUserRepository.save(appUser);

        User user = User.builder()
                .username(username)
                .email(email)
                .name(nome)
                .surname(cognome)
                .city(città)
                .birthDate(dataDiNascita)
                .roles(roles)
                .build();

        userRepository.save(user);

        return appUser;
    }

    public AppUser registerUser(String username, String password, String email, String nome,
                                String cognome, String città, LocalDate dataDiNascita, Set<Role> roles) {

        if (appUserRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRoles(roles);
        appUserRepository.save(appUser);

        User user = User.builder()
                .username(username)
                .email(email)
                .name(nome)
                .surname(cognome)
                .city(città)
                .birthDate(dataDiNascita)
                .roles(roles)
                .build();

        userRepository.save(user);

        return appUser;
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public String authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            throw new SecurityException("Credenziali non valide", e);
        }
    }

    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));
    }
}