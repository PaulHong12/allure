package com.msa.auth;

import com.msa.member.domain.Member;
import com.msa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 사용자 이메일을 가져온다.
        String email = oAuth2User.getAttribute("email");
        // 서비스 제공 플랫폼(GOOGLE, KAKAO, NAVER)이 어디인지 가져온다.
        String provider = oAuth2User.getAttribute("provider");

        // CustomOAuth2UserService에서 셋팅한 로그인한 회원 존재 여부를 가져온다.
        boolean isExist = Boolean.TRUE.equals(oAuth2User.getAttribute("exist"));
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication, email);

        // 회원이 없는 경우 새로 생성
        if (!isExist) {
            Member newMember = new Member(oAuth2User.getName(), email, passwordEncoder.encode(UUID.randomUUID().toString()),
                    Set.of("ROLE_USER"));
            memberRepository.save(newMember);
        }
        response.addCookie(jwtTokenProvider.generateCookie("refreshToken", tokenInfo.refreshToken()));
        response.addCookie(jwtTokenProvider.generateCookie("accessToken", tokenInfo.accessToken()));
        // 홈으로 리다이렉트 시킨다.
        getRedirectStrategy().sendRedirect(request, response, "/"+email+"/home");
    }
}
