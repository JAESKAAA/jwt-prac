package com.prac.jwtserver.config;

import com.prac.jwtserver.filter.MyFilter3;
import com.prac.jwtserver.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
        http.csrf().disable();
        http.authorizeHttpRequests().antMatchers("/h2-console/**", "/member/**").permitAll();

        // for h2-console
        http.headers().frameOptions().disable();
        //세션 사용을 안하기 위한 설정 (Stateless 서버로 설정)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(corsFilter) //@CrossOrigin (인증X), 시큐리티 필터에 등록해야함
            .formLogin().disable()
            .httpBasic().disable() //기본인증이 아닌 토큰을 통한 Bearer방식 사용을 위해 비활성화처리
            .addFilter(new JwtAuthenticationFilter(authenticationManager())) //AuthenticationManager 를 파라미터로 필요로함
            .authorizeRequests()
            .antMatchers("/api/v1/user/**")
            .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
            .antMatchers("/api/v1/manger/**")
            .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
            .antMatchers("/api/v1/admin/**")
            .access("hasRole('ROLE_ADMIN')")
            .anyRequest().permitAll();
    }

}

