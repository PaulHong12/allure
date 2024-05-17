package com.msa.post.repository;

import com.msa.post.domain.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> search(String query);
}
