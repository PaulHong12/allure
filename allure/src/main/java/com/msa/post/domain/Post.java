package com.msa.post.domain;

import com.msa.comment.domain.Comment;
import com.msa.post.dto.PostDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Post {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public long id;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	@Getter
	@Column(name = "creator")
	private String creator;

	@Setter
	@Column(name="title")
	private String title;

	@Setter
	@Column(name="content", columnDefinition="TEXT")
	private String content;

	//
	@Column(name = "date")
	private LocalDate date = LocalDate.now();

	@Getter
	@Setter
	@Column(name="videoId")
	private String videoId;

	@CreatedDate
	private LocalDateTime createdAt = LocalDateTime.now();

	@LastModifiedDate
	private LocalDateTime updatedAt = LocalDateTime.now();


	public Post() {
		super();
	}

	public Post(String title, String content, String username) {
		this.title = title;
		this.content = content;
		this.creator = username;
	}
	public Post(String title, String content, String username, String videoId) {
		this.title = title;
		this.content = content;
		this.creator = username;
		this.videoId = videoId;
	}
	/*public Post(String title, String content, List<Comment> comments) {
		this.title = title;
		this.content = content;
		this.comments = comments;
	}*/

	public PostDto convert2DTO() {
		return new PostDto(this.getTitle(), this.getContent(), this.creator, this.videoId);
	}

	public LocalDate getDate() {
		return this.createdAt.toLocalDate(); //debug
	}

	public void addComment(Comment newComment) {
		if (comments == null) {
			comments = new ArrayList<>();
		}
		newComment.setPost(this, this.id);
		comments.add(newComment);
		//comments.add(new Comment(newComment.getContent(), this, this.id));
	}

}
