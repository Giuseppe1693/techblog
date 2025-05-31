package it.epicode.techblog.posts;

import it.epicode.techblog.comments.CommentRepository;
import it.epicode.techblog.likes.LikeRepository;
import it.epicode.techblog.users.User;
import it.epicode.techblog.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public Post createPost(Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        post.setAuthor(user);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public Page<PostResponse> getAllPostResponses(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post non trovato"));
    }

    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthor(author);
    }

    public Post updatePost(Long id, Post updatedPost) {
        Post post = getPostById(id);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setCoverUrl(updatedPost.getCoverUrl());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = getPostById(id);
        likeRepository.deleteAll(likeRepository.findByPost(post));
        commentRepository.deleteAll(commentRepository.findByPost(post));
        postRepository.delete(post);
    }

    public PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                // .content(post.getContent()) // rimosso per la home
                .coverUrl(post.getCoverUrl())
                .createdAt(post.getCreatedAt())
                .authorName(post.getAuthor().getName())
                .authorUsername(post.getAuthor().getUsername())
                .authorAvatarUrl(post.getAuthor().getAvatarUrl())
                .build();
    }

    public PostResponse toDetailedResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .coverUrl(post.getCoverUrl())
                .createdAt(post.getCreatedAt())
                .authorName(post.getAuthor().getName())
                .authorUsername(post.getAuthor().getUsername())
                .authorAvatarUrl(post.getAuthor().getAvatarUrl())
                .build();
    }
}
