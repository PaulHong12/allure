package com.msa.config;

import com.msa.auth.AuthenticationSuccessHandler;
import com.msa.auth.JwtAuthFilter;
import com.msa.member.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOAuth2UserService oAuth2UserService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 자원에 대해서 Security를 적용하지 않음으로 설정
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/", "/login", "/signUp", "/members/auth/**",
                        "/h2-console/**", "/style.css")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .successHandler(authenticationSuccessHandler)
                .userInfoEndpoint()
                .userService(oAuth2UserService);

        return http.build();

    }
}
