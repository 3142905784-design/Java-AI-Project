package com.theo.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*") //拦截所有请求
public class FilterDemo implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //初始化方法, web服务器启动的时候执行, 只执行一次
        Filter.super.init(filterConfig);
        log.info("init初始化方法, web服务器启动的时候执行, 只执行一次");
    }

    //过滤方法, 每次请求都会执行
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        log.info("拦截到了请求.... 放行前 .... ");

        //放行, 让请求继续往下走
        filterChain.doFilter(servletRequest, servletResponse);
        log.info("拦截到了请求.... 放行后 .... ");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
        log.info("destroy销毁方法, web服务器关闭的时候执行, 只执行一次");
    }
}
