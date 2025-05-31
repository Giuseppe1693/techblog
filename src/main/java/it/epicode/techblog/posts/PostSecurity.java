package it.epicode.techblog.posts;

import it.epicode.techblog.auth.Role;
import it.epicode.techblog.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("postSecurity")
@RequiredArgsConstructor
public class PostSecurity {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public boolean isPostAuthor(Long postId, String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    boolean isAdmin = user.getRoles().stream()
                            .anyMatch(role -> role == Role.ROLE_ADMIN);
                    if (isAdmin) return true;

                    return postRepository.findById(postId)
                            .map(post -> post.getAuthor().getUsername().equals(username))
                            .orElse(false);
                }).orElse(false);
    }
}
