package com.ecommerce.service;

import com.ecommerce.dto.ChangePasswordDTO;
import com.ecommerce.dto.ForgotPasswordDTO;
import com.ecommerce.dto.SendForgotSmsDTO;
import com.ecommerce.dto.SendLoginSmsDTO;
import com.ecommerce.dto.SmsLoginDTO;
import com.ecommerce.dto.UserLoginDTO;
import com.ecommerce.dto.UserRegisterDTO;
import com.ecommerce.dto.UserUpdateDTO;
import com.ecommerce.entity.User;

public interface UserService {
    String register(UserRegisterDTO dto);
    String login(UserLoginDTO dto);

    void sendLoginSms(SendLoginSmsDTO dto);

    void sendForgotSms(SendForgotSmsDTO dto);

    String loginBySms(SmsLoginDTO dto);

    User getUserInfo(Long userId);
    void updateUserInfo(Long userId, UserUpdateDTO dto);

    void changePassword(Long userId, ChangePasswordDTO dto);

    void forgotPassword(ForgotPasswordDTO dto);
}
