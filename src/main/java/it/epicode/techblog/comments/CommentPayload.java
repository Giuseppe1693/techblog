package it.epicode.techblog.comments;

import lombok.Data;

@Data
public class CommentPayload {
    private String content;
    private Long postId;

    public Comment toComment() {
        Comment comment = new Comment();
        comment.setContent(this.content);
        return comment;
    }
}
