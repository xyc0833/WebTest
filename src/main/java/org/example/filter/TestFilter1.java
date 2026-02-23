package org.example.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
// "/ *" 拦截所有的请求
@WebFilter("/*")
public class TestFilter1 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("我是1号过滤器");
        System.out.println(request.getRequestURL());
        //doFilter的核心作用：过滤器链的核心放行方法
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
