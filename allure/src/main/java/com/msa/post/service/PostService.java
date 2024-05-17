package com.msa.post.service;

import com.msa.post.domain.Post;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostService {
	
	Post addPost(String title, String content, String username, String keyword);
	
	Optional<Post> getPost(long id);
	
	List<Post> getPostListByUserId();

	List<Post> getPostList();

	void deletePost(long id);

  //  Post updatePost(String title, String content);

	//Post addComment(long i, String content);

    List<Post> getPostsByDate(LocalDate parsedDate);

	List<Post> getPostsByDateRange(LocalDate start, LocalDate end);

	Post updatePost(long postId, String title, String content, String videoId);

    List<Post> getPostsByDateAndMember(LocalDate now, String username);

	List<Post> getPostsByDateRangeAndMember(LocalDate start, LocalDate end, String username);

	Optional<Post> findById(Long postId);

	Post addComment(long postId, String s, String s1);

    Post getPostById(long postId);
}
