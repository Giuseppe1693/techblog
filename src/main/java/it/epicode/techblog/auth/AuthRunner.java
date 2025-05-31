package it.epicode.techblog.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

//@Component
public class AuthRunner implements ApplicationRunner {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<AppUser> adminUser = appUserService.findByUsername("admin");
        if (adminUser.isEmpty()) {
            appUserService.registerUser(
                    "admin",
                    "adminpwd",
                    "admin@example.com",
                    "Admin",
                    "Root",
                    "Roma",
                    LocalDate.of(1980, 1, 1)
            );
        }

        Optional<AppUser> normalUser = appUserService.findByUsername("user");
        if (normalUser.isEmpty()) {
            appUserService.registerUser(
                    "user",
                    "userpwd",
                    "user@example.com",
                    "Mario",
                    "Rossi",
                    "Milano",
                    LocalDate.of(1995, 5, 5)
            );
        }

        Optional<AppUser> normalSeller = appUserService.findByUsername("seller");
        if (normalSeller.isEmpty()) {
            appUserService.registerUser(
                    "seller",
                    "sellerpwd",
                    "seller@example.com",
                    "Lucia",
                    "Verdi",
                    "Torino",
                    LocalDate.of(1992, 8, 20)
            );
        }
    }
}
