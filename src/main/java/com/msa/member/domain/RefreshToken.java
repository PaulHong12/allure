package com.msa.member.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id; // No need for @Column(columnDefinition = "BINARY(16)"), PostgreSQL handles UUID natively

    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", unique = true) // Ensure the referencedColumnName is set to the primary key of the Member entity
    private Member member;

    public RefreshToken(String refreshToken, Member member) {
        this.refreshToken = refreshToken;
        this.member = member;
    }

    // Hibernate requires a no-args constructor
    public RefreshToken() {

    }

}