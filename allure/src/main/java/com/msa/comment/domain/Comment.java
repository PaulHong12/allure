package com.msa.comment.domain;

import com.msa.comment.dto.CommentDto;
import com.msa.post.domain.Post;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "comment")
@EntityListeners(AuditingEntityListener.class)
public class Comment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column
    public String nickName;

    @Column(name = "content")
    private String content;

    //many comments to one post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @CreationTimestamp
    public LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();


    public void setPost(Post post, Long postId) {
        this.post = post;
    }

    //이거 nickname(comment author)도 저장해야함.
    public Comment(String content, Post post) {
        this.content = content;
        this.post = post;
    }

    public Comment(String content, Post post, String nickName) {
        this.content = content;
        this.post = post;
        this.nickName = nickName;
    }

    public Comment() {

    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public Object convertToDTO() {
        CommentDto dto = new CommentDto();
        dto.setId(this.id);       // Set the id from Comment to CommentDto
        dto.setContent(this.content); // Set the content from Comment to CommentDto
        return dto;
    }
}