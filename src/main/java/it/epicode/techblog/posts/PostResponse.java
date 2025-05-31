package it.epicode.techblog.posts;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String coverUrl;
    private LocalDateTime createdAt;
    private String authorName;
    private String authorUsername;
    private String authorAvatarUrl;
}