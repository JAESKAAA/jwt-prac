package com.prac.jwtserver.filter;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {


        System.out.println("필터1");
        chain.doFilter(request, response); //체인 안걸고 printWriter등을 쓰면 다음 필터로 안넘어 가고 프로그램이 종료되어 버림 주의!
    }
}
