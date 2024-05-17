package com.msa.comment.controller;

import com.msa.comment.CommentService.CommentService;
import com.msa.comment.domain.Comment;
import com.msa.comment.dto.CommentDto;
import com.msa.member.service.MemberService;
import com.msa.post.dto.ResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private MemberService memberService;
    // ... other methods ...

    // Update a comment
    @PutMapping("/{commentId}")
    public ResponseEntity<ResultDto<Object>> updateComment(
            @PathVariable long commentId,
            @RequestBody CommentDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;
        Comment updatedComment = null;
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUsername = authentication.getName(); // Gets the username of the currently logged-in user
        }
        Comment comment = commentService.FindById(commentId);
        currentUsername = memberService.findByUsername(currentUsername).getEmail();
        if(comment.getNickName().equals(currentUsername)) {
            updatedComment = commentService.updateComment(commentId, dto.getContent());
        }
        if (updatedComment != null) {
            return ResponseEntity.ok(new ResultDto<>(200, "Comment updated", updatedComment.convertToDTO()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResultDto<>(404, "Comment not found", null));
        }
    }

    // Delete a comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResultDto<Void>> deleteComment(
            @PathVariable long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;
        boolean deleted = false;
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUsername = authentication.getName(); // Gets the username of the currently logged-in user
        }
        Comment comment = commentService.FindById(commentId);
        currentUsername = memberService.findByUsername(currentUsername).getEmail();
        if(comment.getNickName().equals(currentUsername)) {
            deleted = commentService.deleteComment(commentId);
        }
        if (deleted) {
            return ResponseEntity.ok(new ResultDto<>(200, "Comment deleted", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResultDto<>(404, "Comment not found", null));
        }
    }
}
