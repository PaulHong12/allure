package com.msa.member.dto;

public record LoginDto(String email, String password
) {
    public String username() {
        return this.username();
    }
}
