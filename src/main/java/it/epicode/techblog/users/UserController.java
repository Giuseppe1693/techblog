package it.epicode.techblog.users;

import com.cloudinary.Cloudinary;
import it.epicode.techblog.posts.Post;
import it.epicode.techblog.posts.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateMyProfile(@RequestBody @Valid User updatedUser, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.updateByUsername(username, updatedUser);
        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadMyAvatar(@RequestPart("file") MultipartFile file, Authentication authentication) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Cloudinary.asMap("folder", "avatars", "public_id", file.getOriginalFilename())
            );

            String url = result.get("secure_url").toString();

            String username = authentication.getName();
            User user = userService.getByUsername(username);
            user.setAvatarUrl(url);

            userRepository.save(user);

            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Errore durante l'upload dell'immagine");
        }
    }

    @GetMapping("/me/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Post>> getMyPosts(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        List<Post> myPosts = postRepository.findByAuthor(user);
        return ResponseEntity.ok(myPosts);
    }
}
