package com.msa.member.service;

import com.msa.auth.JwtTokenProvider;
import com.msa.auth.TokenInfo;
import com.msa.member.domain.Member;
import com.msa.member.domain.RefreshToken;
import com.msa.member.repository.MemberRepository;
import com.msa.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //import JwtAuthFilter to extract token from HTTPServletRequest.
    //add an invalidating method in JwtTokenProvider.

    public Member addUser(String userName, String email, String password) {
        Member member = new Member(userName, email, passwordEncoder.encode(password),
                Set.of("USER"));
        return memberRepository.save(member);
    }

    @Transactional
    public TokenInfo login(String email, String password) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManagerBuilder
                .getObject().authenticate(authenticationToken);
        Optional<Member> member = memberRepository.findByEmail(email);

       // if (member.isEmpty()) {
         //   throw new AccessDeniedException("not found user");
        //}

        // 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication, email);

        // refresh token 없으면 생성 or update
        refreshTokenRepository.findByMember_Email(member.get().getEmail())
                .ifPresentOrElse(refreshToken -> {
                    refreshToken.setRefreshToken(tokenInfo.refreshToken());
                    refreshTokenRepository.save(refreshToken);
                }, () -> {
                    refreshTokenRepository.save(new RefreshToken(tokenInfo.refreshToken(), member.get()));
                });

        return tokenInfo;
    }

    public Member findByUsername(String username) {
        // Assuming you have a MemberRepository
        return memberRepository.findByUsername(username);
    }

    public Member getLoggedInUser(HttpServletRequest request) {
        String username = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            username = authentication.getName();
        }
        return (username != null) ? findByUsername(username) : null;
    }

    public boolean isFriend(UUID profileUserId, UUID loggedInUserId) {
        if (profileUserId.equals(loggedInUserId)) {
            return true; // A user is always a friend of themselves
        }
        Member profileUser = memberRepository.findById(profileUserId).orElse(null);
        Member loggedInUser = memberRepository.findById(loggedInUserId).orElse(null);

        if (profileUser == null || loggedInUser == null) {
            return false;
        }

        return profileUser.getFriends().contains(loggedInUser) && loggedInUser.getFriends().contains(profileUser);
    }

    public Optional<Member> findByEmail(String username) {
        return memberRepository.findByEmail(username);
    }

    public List<Member> getNonFriendsMembers(String currentUsername) {
        // Fetch the current user
        Member currentUser = memberRepository.findByUsername(currentUsername);

        // Fetch all members
        List<Member> allMembers = memberRepository.findAll();

        // Filter out the current user and their friends
        return allMembers.stream()
                .filter(member -> !member.equals(currentUser) && !currentUser.getFriends().contains(member))
                .collect(Collectors.toList());
    }

    public List<Member> getFriendList(String currentUsername) {
        // Fetch the current user
        Member currentUser = memberRepository.findByUsername(currentUsername);

        // Check for mutual friendship
        return currentUser.getFriends().stream()
                .filter(friend -> friend.getFriends().contains(currentUser) && currentUser.getFriends().contains(friend))
                .collect(Collectors.toList());
    }


    public Optional<Member> findById(UUID memberId) {
        return memberRepository.findById(memberId);
    }

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Set<Member> findReceivedFriendRequests(String username) {
        Member currentUser = memberRepository.findByUsername(username);
        if (currentUser != null) {
            return currentUser.getReceivedFriendRequests();
        } else {
            // Handle the case where the user does not exist or is not found
            return Collections.emptySet();
        }
    }


}
