package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.CaptchaVO;
import com.ecommerce.dto.ChangePasswordDTO;
import com.ecommerce.dto.ForgotPasswordDTO;
import com.ecommerce.dto.SendForgotSmsDTO;
import com.ecommerce.dto.SendLoginSmsDTO;
import com.ecommerce.dto.SmsLoginDTO;
import com.ecommerce.dto.UserLoginDTO;
import com.ecommerce.dto.UserRegisterDTO;
import com.ecommerce.dto.UserUpdateDTO;
import com.ecommerce.entity.User;
import com.ecommerce.service.CaptchaService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    /** 图形验证码（登录发短信前使用） */
    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha() {
        return Result.success(captchaService.generate());
    }

    /** 校验图形验证码后发送登录短信（演示：验证码见后端日志） */
    @PostMapping("/sms/send-login")
    public Result<Void> sendLoginSms(@Valid @RequestBody SendLoginSmsDTO dto) {
        userService.sendLoginSms(dto);
        return Result.success();
    }

    @PostMapping("/sms/send-forgot")
    public Result<Void> sendForgotSms(@Valid @RequestBody SendForgotSmsDTO dto) {
        userService.sendForgotSms(dto);
        return Result.success();
    }

    /** 短信验证码登录 */
    @PostMapping("/login/sms")
    public Result<String> loginSms(@Valid @RequestBody SmsLoginDTO dto) {
        return Result.success(userService.loginBySms(dto));
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) {
        String token = userService.register(dto);
        return Result.success(token);
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginDTO dto) {
        String token = userService.login(dto);
        return Result.success(token);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestAttribute("userId") Long userId, @RequestBody UserUpdateDTO dto) {
        userService.updateUserInfo(userId, dto);
        return Result.success();
    }

    @PostMapping("/forgot")
    public Result<Void> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        userService.forgotPassword(dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestAttribute("userId") Long userId, @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(userId, dto);
        return Result.success();
    }
}
