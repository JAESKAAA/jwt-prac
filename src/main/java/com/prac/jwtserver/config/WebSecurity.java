package com.prac.jwtserver.config;

import com.prac.jwtserver.filter.MyFilter3;
import com.prac.jwtserver.jwt.JwtAuthenticationFilter;
import com.prac.jwtserver.jwt.JwtAuthorizationFilter;
import com.prac.jwtserver.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //커스텀 필터를 그냥 addFilter로 등록시, 시큐리티 필터에 등록 되어 있지 않기 때문에 Bean creation 에러 발생
        //따라서, 시큐리티 필터 전 후로 필터설정을 해줘야하는데, 그때 쓰이는게 add 또는 before필터임
        //시큐리티 필터 체인은 커스텀 보다 먼저 실행되게 되어있음. (그래서 마이필터1,2가 나중에 실행되는 것이며, after설정으로 바꿔도 마찬가지임)
        //시큐리티 필터 체인 순서는 구글링해서 참고하용
       http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
        http.csrf().disable();
        http.authorizeHttpRequests().antMatchers("/h2-console/**", "/member/**").permitAll();

        // for h2-console
        http.headers().frameOptions().disable();
        //세션 사용을 안하기 위한 설정 (Stateless 서버로 설정)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(corsFilter) //@CrossOrigin (인증X), 시큐리티 필터에 등록해야함
            .formLogin().disable() //폼 로그인 안쓴다는 내용
            .httpBasic().disable() //기본인증이 아닌 토큰을 통한 Bearer방식 사용을 위해 비활성화처리
            .addFilter(new JwtAuthenticationFilter(authenticationManager())) //AuthenticationManager 를 파라미터로 필요로함
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository)) //AuthenticationManager 를 파라미터로 필요로함
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

