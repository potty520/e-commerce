package com.ecommerce.config;

import com.ecommerce.interceptor.AdminRoleInterceptor;
import com.ecommerce.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminRoleInterceptor adminRoleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .order(0)
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/api/user/login/sms",
                        "/api/user/captcha",
                        "/api/user/sms/send-login",
                        "/api/user/sms/send-forgot",
                        "/api/user/forgot",
                        "/api/product/list",
                        "/api/product/detail/**",
                        "/api/product/review/list/**",
                        "/api/category/list",
                        "/api/pay/mock/callback",
                        "/api/payment/unified/callback",
                        "/api/payment/notify/alipay",
                        "/api/payment/notify/wechat"
                );
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns("/api/admin/**")
                .order(1);
    }
}
