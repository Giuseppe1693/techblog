package it.epicode.techblog.posts;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody Post post) {
        Post saved = postService.createPost(post);
        return ResponseEntity.ok(postService.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(postService.getAllPostResponses(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(postService.toDetailedResponse(post));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @postSecurity.isPostAuthor(#id, authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        Post updated = postService.updatePost(id, updatedPost);
        return ResponseEntity.ok(postService.toResponse(updated));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or @postSecurity.isPostAuthor(#id, authentication.name)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
