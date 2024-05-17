package com.msa.comment.CommentService;

import com.msa.comment.domain.Comment;
import com.msa.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public Comment updateComment(long commentId, String content) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setContent(content);
            return commentRepository.save(comment);
        } else {
            return null; // Comment not found
        }
    }

    // Delete a comment by its ID
    public boolean deleteComment(long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            commentRepository.delete(optionalComment.get());
            return true;
        } else {
            return false;
        }
    }

    public Comment FindById(long commentId) {
        return commentRepository.findById(commentId).get();
    }
}