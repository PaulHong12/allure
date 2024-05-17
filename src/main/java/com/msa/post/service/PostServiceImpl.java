package com.msa.post.service;

import com.msa.comment.domain.Comment;
import com.msa.comment.repository.CommentRepository;
import com.msa.member.repository.MemberRepository;
import com.msa.post.domain.Post;
import com.msa.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

	//private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public PostServiceImpl(MemberRepository memberRepository, PostRepository postRepository, CommentRepository commentRepository) {
	//	this.memberRepository = memberRepository;
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
	}


	@Override
	public Post addPost(String title, String content, String username, String videoId) {
		// 테스트 코드가 동작하도록 구현
		Post post = new Post(title, content, username, videoId);
		return postRepository.save(post);
	}

	@Override
	public Optional<Post> getPost(long id) {
		return postRepository.findById(id);
	}

	//구현하기
	@Override
	public List<Post> getPostListByUserId() {
		return new ArrayList<>();
	}

	@Override
	// findAll 을 통해서 모든 데이터 조회
	// created_at 기준 내림차순으로 정렬
	public List<Post> getPostList() {
		return postRepository.findAll()
				.stream()
				.sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).toList();
	}

	@Transactional
	public Post addComment(long postId, String content, String nickName) {
		//Optional<Post> postOptional = postRepository.findById(postId);
		//if (postOptional.isPresent()) {
			//Post post = postOptional.get();
			System.out.print(postId);
			//이게 문제인듯. findByCreatorAndDateBetween()사용해보기..
			Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));

			Comment newComment = new Comment(content, post, nickName);
			newComment.setPost(post, postId);
			commentRepository.save(newComment);// This should save both post and comment due to CascadeType.ALL
		//} else {tId
		//	throw new IllegalArgumentException("Post with ID " + postId + " not found.");
		//}
		return post;
	}

	@Override
	public Post getPostById(long postId) {
		return postRepository.findById(postId).get();
	}

	@Override
	public List<Post> getPostsByDate(LocalDate date) {
		return postRepository.findAllByDate(date);
	}

	@Override
	public List<Post> getPostsByDateRange(LocalDate start, LocalDate end) {
		return postRepository.findAllByDateBetween(start, end);
	}

	@Transactional
	public Post updatePost(long postId, String title, String content,  String videoId) {
		Optional<Post> postOptional = postRepository.findById(postId);

		if (postOptional.isPresent()) {
			Post existingPost = postOptional.get();
			existingPost.setTitle(title);
			existingPost.setContent(content);
			existingPost.setVideoId(videoId);
			return postRepository.save(existingPost); // Save the updated post
		} else {
			// Handle the case where the post does not exist
			throw new IllegalArgumentException("Post with ID " + postId + " not found.");
		}
	}

	@Override
	public List<Post> getPostsByDateAndMember(LocalDate now, String username) {
		return null;
	}

	@Override
	public List<Post> getPostsByDateRangeAndMember(LocalDate start, LocalDate end, String username) {
		return postRepository.findByCreatorAndDateBetween(username, start, end);
	}

	@Override
	public Optional<Post> findById(Long postId) {
		return postRepository.findById(postId);
	}

	@Override
	public void deletePost(long id) {
		postRepository.deleteById(id);
	}

}
