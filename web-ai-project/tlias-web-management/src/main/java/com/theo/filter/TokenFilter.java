package com.theo.filter;

import com.theo.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
@WebFilter(urlPatterns = "/*") //拦截所有请求
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       HttpServletRequest request=(HttpServletRequest)servletRequest;
       HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求路径
        String requestURI = request.getRequestURI();

        //如果请求路径是登录接口, 则直接放行
        if(requestURI.equals("/login")){
            log.info("登录接口, 直接放行");
            filterChain.doFilter(request, response);
            return;
        }

        //获取请求头中的token
        String token = request.getHeader("token");
        //如果token不存在, 则返回错误
        if(token == null || token.isEmpty()) {
            log.info("token不存在, 返回错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("token不存在");
            return;
        }

        // 如果token存在, 校验令牌, 如果校验失败 -> 返回错误信息(响应401状态码)
        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("令牌非法, 响应401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 校验通过, 放行
        log.info("令牌合法, 放行");
        filterChain.doFilter(request, response);
    }

}
