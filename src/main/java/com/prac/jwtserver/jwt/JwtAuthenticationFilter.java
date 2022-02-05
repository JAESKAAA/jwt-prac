package com.prac.jwtserver.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prac.jwtserver.auth.PrincipalDetails;
import com.prac.jwtserver.entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있는데,
 * login요청해서 username, password 전송 하면(post)
 * UsernamePasswordAuthenticationFilter가 동작함
 *
 * 근데, config에 formLogin().disable()처리 되어 있어 동작하지 않음
 * 따라서, 다시 시큐리티 config에 등록해줘야 동작하게 됨
 */

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter 로그인 시도");

        //1. username, password 받아서
        try {
            /**
             * 전통적인 방식
             */
//            BufferedReader br = request.getReader();
//
//            String input = null;
//
//            while ((input = br.readLine()) != null) {
//                System.out.println(input);
        //}
        /**
         * ObjectMapper사용
         */
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println("user = " + user);

            UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            //principalDetailsService의 로드바이유저네임 실행됨 -> username만 받아줌 password는 스프링이 알아서 처리함
            //DB에있는 usernamerhk password가 일치한다는 것이 검증되어 authenntication 객체가 생성된 것
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("principalDetails = " + principalDetails.getUser().getUsername());

            //authentication 객체가 session영역에 저장됨
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("================================");
        //2. 정상 인지 로그인 시도 (authenticationManager 로 로그인 시도 -> PrincipalDetailsService호출)

        //3. PrincipalDetailsService의 loadByUsername() 실행됨

        //4. PrincipalDetails를 세션에 담고 (세션에 안 담으면 권한 관리가 안됨)

        //5. JWT 토큰을 만들어서 응답해주면 됨

      return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행 됨 : 인증완료");
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
