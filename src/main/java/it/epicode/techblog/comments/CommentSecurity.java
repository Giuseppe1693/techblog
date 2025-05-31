package it.epicode.techblog.comments;

import it.epicode.techblog.auth.Role;
import it.epicode.techblog.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentSecurity {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public boolean isAuthor(Long commentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .map(user -> {
                    boolean isAdmin = user.getRoles().stream()
                            .anyMatch(role -> role == Role.ROLE_ADMIN);
                    if (isAdmin) return true;

                    return commentRepository.findById(commentId)
                            .map(comment -> comment.getAuthor().getId().equals(user.getId()))
                            .orElse(false);
                }).orElse(false);
    }
}
