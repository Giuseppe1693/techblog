package it.epicode.techblog.likes;

import it.epicode.techblog.posts.Post;
import it.epicode.techblog.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostAndUser(Post post, User user);

    long countByPost(Post post);

    void deleteByPost(Post post);

    List<Like> findByUser(User user);

    List<Like> findByPost(Post post);
}
