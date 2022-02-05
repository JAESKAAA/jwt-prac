package com.prac.jwtserver.config;

import com.prac.jwtserver.filter.MyFilter1;
import com.prac.jwtserver.filter.MyFilter2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 커스텀 필터를 만들었는데, 디폴트 실행 순서는 시큐리티 필터가 모두 끝난뒤 실행 된다.
 * 따라서, 시큐리티 필터보다 먼저 실행되게 하고 싶으면, webSecurirt config에
 * addBeforeFilter메소드를 걸어서 원하는 SecurityContextPersistenceFilter.class 매개변수로
 * 설정 해주면 됨. 자세한 것은 WebSecurity클래스의 addFilterBefore() 정의된 부분 참고
 *
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyFilter1> filter1() {

        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
        bean.addUrlPatterns("/*");
        bean.setOrder(0); //낮은 번호가 필터중에서 가장 먼저 실행됨
        return bean;
    }

    @Bean
    public FilterRegistrationBean<MyFilter2> filter2() {

        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
        bean.addUrlPatterns("/*");
        bean.setOrder(1); //낮은 번호가 필터중에서 가장 먼저 실행됨
        return bean;
    }
}
