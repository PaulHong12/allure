package com.msa.post.dto;

public record ResultDto<T>(
        int code,
        String message,
        T data
) {

}
