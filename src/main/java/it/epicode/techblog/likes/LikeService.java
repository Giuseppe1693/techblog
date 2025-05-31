package it.epicode.techblog.likes;

import it.epicode.techblog.posts.Post;
import it.epicode.techblog.posts.PostRepository;
import it.epicode.techblog.users.User;
import it.epicode.techblog.users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @PreAuthorize("isAuthenticated()")
    public void toggleLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trovato"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato"));

        likeRepository.findByPostAndUser(post, user).ifPresentOrElse(
                likeRepository::delete,
                () -> {
                    Like like = new Like();
                    like.setPost(post);
                    like.setUser(user);
                    likeRepository.save(like);
                }
        );
    }

    public long countLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post non trovato"));
        return likeRepository.countByPost(post);
    }
}
