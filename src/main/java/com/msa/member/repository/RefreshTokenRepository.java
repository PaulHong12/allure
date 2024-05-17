package com.msa.member.repository;

import com.msa.member.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByMember_Email(String email);
    Optional<RefreshToken> findByMember_Username(String email);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
