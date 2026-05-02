package com.ecommerce.interceptor;

import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object r = request.getAttribute("role");
        int role = r instanceof Integer ? (Integer) r : 0;
        if (role != Constants.UserRole.ADMIN) {
            throw new BusinessException(403, "需要管理员权限");
        }
        return true;
    }
}
