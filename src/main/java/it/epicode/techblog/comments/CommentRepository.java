package it.epicode.techblog.comments;

import it.epicode.techblog.posts.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findByPostId(Long postId);

    @Transactional
    void deleteByPost(Post post);
}
