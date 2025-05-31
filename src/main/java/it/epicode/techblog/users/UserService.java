package it.epicode.techblog.users;

import it.epicode.techblog.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AppUserRepository appUserRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setEmail(updatedUser.getEmail());
        user.setCity(updatedUser.getCity());
        user.setBirthDate(updatedUser.getBirthDate());
        user.setAvatarUrl(updatedUser.getAvatarUrl());
        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));
    }

    public User updateByUsername(String username, User updatedUser) {
        User existingUser = getByUsername(username);
        existingUser.setName(updatedUser.getName());
        existingUser.setSurname(updatedUser.getSurname());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setBirthDate(updatedUser.getBirthDate());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        deleteByUsername(user.getUsername());
    }

    @Transactional
    public void deleteByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        clearRelationsAndDelete(user);

        appUserRepository.deleteByUsername(username);
    }

    private void clearRelationsAndDelete(User user) {
        var commentsCopy = new ArrayList<>(user.getComments());
        var likesCopy = new ArrayList<>(user.getLikes());
        var postsCopy = new ArrayList<>(user.getPosts());

        commentsCopy.forEach(comment -> comment.setAuthor(null));
        user.getComments().clear();

        likesCopy.forEach(like -> like.setUser(null));
        user.getLikes().clear();

        postsCopy.forEach(post -> {
            post.getComments().forEach(comment -> comment.setPost(null));
            post.getLikes().forEach(like -> like.setPost(null));
            post.setAuthor(null);
        });
        user.getPosts().clear();

        userRepository.delete(user);
    }
}
