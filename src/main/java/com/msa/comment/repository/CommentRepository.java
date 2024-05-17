package com.msa.comment.repository;

import com.msa.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(long commentId);
}
