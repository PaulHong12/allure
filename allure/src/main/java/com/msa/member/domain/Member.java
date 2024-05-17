package com.msa.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.msa.member.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Member implements UserDetails {

    //@Id
    //@GeneratedValue(generator = "uuid2")
    //@GenericGenerator(name = "uuid2", strategy = "uuid2")

    @Id
    @GeneratedValue
    @Type(type="org.hibernate.type.UUIDCharType") // This line is optional, Hibernate usually detects the type automatically.
    public UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name="username")
    private String username;

    @JsonIgnore
    @Column(name="password")
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();


    @Getter
    @ManyToMany
    @JoinTable(
            name = "member_friends",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Member> friends = new HashSet<>();

    @Getter
    @ManyToMany
    @JoinTable(
            name = "member_friend_requests",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_requester_id")
    )
    private Set<Member> receivedFriendRequests = new HashSet<>();

    // 양방향 관계., 누구의 친구인지 알수있음.
    @Getter
    @ManyToMany(mappedBy = "friends")
    private Set<Member> friendOf = new HashSet<>();

    public UserDto toDTO() {
        return new UserDto(this.username, this.email);
    }

    // Hibernate에 의해 필요합니다.
    public Member() {

    }
    public Member(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public Member(String username, String email, String password, Set<String> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setFriends(Set<Member> friends) {
        this.friends = friends;
    }

    // Method to handle receiving a friend request
    public void receiveFriendRequest(Member member) {
        this.receivedFriendRequests.add(member);
    }

    // Method to handle accepting a friend request
    public void acceptFriendRequest(Member member) {
        if (receivedFriendRequests.contains(member)) {
            friends.add(member);
            member.getFriends().add(this); // Mutual friendship
            receivedFriendRequests.remove(member);
        }
    }

    // Method to handle declining a friend request
    public void declineFriendRequest(Member member) {
        receivedFriendRequests.remove(member);
    }

    // Existing addFriend method updated to send a friend request
    public void addFriend(Member friend) {
        friend.receiveFriendRequest(this);
    }

    // Updated removeFriend method
    public void removeFriend(Member friend) {
        this.friends.remove(friend);
        friend.getFriends().remove(this); // Remove from both sides
    }

    public void setFriendOf(Set<Member> friendOf) {
        this.friendOf = friendOf;
    }
}