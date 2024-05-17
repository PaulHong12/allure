package com.msa.auth;

import com.msa.member.domain.Member;
import com.msa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");

        boolean isExist = oAuth2User.getAttribute("exist");

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication, email);

        if (!isExist) {
            Member newMember = new Member(
                    oAuth2User.getName(),
                    email,
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    Set.of("ROLE_USER")
            );
            memberRepository.save(newMember);
        }

        //HTTPServletResponse에 쿠키를 더해서 줄 수 있다.
        response.addCookie(jwtTokenProvider.generateCookie("accessToken", tokenInfo.accessToken()));
        response.addCookie(jwtTokenProvider.generateCookie("refreshToken", tokenInfo.refreshToken()));

        // why?!
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
