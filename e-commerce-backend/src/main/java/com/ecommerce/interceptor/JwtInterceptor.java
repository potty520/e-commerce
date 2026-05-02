package com.ecommerce.interceptor;

import com.ecommerce.common.BusinessException;
import com.ecommerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "Please login first");
        }
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "Token expired, please login again");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
        return true;
    }
}
