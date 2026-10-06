package com.internship.interceptor;

import com.internship.util.JwtUtil;
import com.internship.util.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        // 放行登录、注册、静态资源、Swagger
        if (uri.contains("/login") || uri.contains("/register") || uri.contains("/doc.html")
                || uri.contains("/swagger") || uri.contains("/webjars") || uri.contains("/v2/api-docs")
                || uri.contains("/favicon.ico") || uri.startsWith("/static") || !uri.startsWith("/api")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.error(401, "未登录或Token无效")));
            return false;
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Result.error(401, "Token已过期")));
            return false;
        }

        // 将用户信息存入request属性，供Controller使用
        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        return true;
    }
}
