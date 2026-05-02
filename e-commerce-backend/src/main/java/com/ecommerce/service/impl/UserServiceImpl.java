package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Constants;
import com.ecommerce.dto.ChangePasswordDTO;
import com.ecommerce.dto.ForgotPasswordDTO;
import com.ecommerce.dto.SendForgotSmsDTO;
import com.ecommerce.dto.SendLoginSmsDTO;
import com.ecommerce.dto.SmsLoginDTO;
import com.ecommerce.dto.UserLoginDTO;
import com.ecommerce.dto.UserRegisterDTO;
import com.ecommerce.dto.UserUpdateDTO;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.service.CaptchaService;
import com.ecommerce.service.SmsCodeService;
import com.ecommerce.service.UserService;
import com.ecommerce.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private SmsCodeService smsCodeService;

    @Override
    public String register(UserRegisterDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("Username already exists");
        }
        if (dto.getPhone() != null) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, dto.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException("Phone number already registered");
            }
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(md5Password(dto.getPassword()));
        user.setStatus(Constants.UserStatus.NORMAL);
        user.setRole(Constants.UserRole.USER);
        userMapper.insert(user);
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public String login(UserLoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(401, "Username or password incorrect");
        }
        if (!user.getPassword().equals(md5Password(dto.getPassword()))) {
            throw new BusinessException(401, "Username or password incorrect");
        }
        if (user.getStatus().equals(Constants.UserStatus.DISABLED)) {
            throw new BusinessException(403, "Account has been disabled");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername(),
                user.getRole() == null ? Constants.UserRole.USER : user.getRole());
    }

    @Override
    public void sendLoginSms(SendLoginSmsDTO dto) {
        captchaService.verifyAndConsume(dto.getCaptchaId(), dto.getCaptchaCode());
        String phone = dto.getPhone().trim();
        LambdaQueryWrapper<User> w = new LambdaQueryWrapper<>();
        w.eq(User::getPhone, phone);
        if (userMapper.selectCount(w) == 0) {
            throw new BusinessException("该手机号未注册");
        }
        smsCodeService.issueLoginSms(phone);
    }

    @Override
    public String loginBySms(SmsLoginDTO dto) {
        String phone = dto.getPhone().trim();
        smsCodeService.verifyLoginSms(phone, dto.getSmsCode());
        LambdaQueryWrapper<User> w = new LambdaQueryWrapper<>();
        w.eq(User::getPhone, phone);
        User user = userMapper.selectOne(w);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        if (user.getStatus().equals(Constants.UserStatus.DISABLED)) {
            throw new BusinessException(403, "账号已被禁用");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername(),
                user.getRole() == null ? Constants.UserRole.USER : user.getRole());
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setId(userId);
        userMapper.updateById(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!user.getPassword().equals(md5Password(dto.getOldPassword()))) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(md5Password(dto.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public void sendForgotSms(SendForgotSmsDTO dto) {
        captchaService.verifyAndConsume(dto.getCaptchaId(), dto.getCaptchaCode());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername().trim());
        User user = userMapper.selectOne(wrapper);
        if (user == null || user.getPhone() == null || !user.getPhone().equals(dto.getPhone().trim())) {
            throw new BusinessException("用户名与手机号不匹配");
        }
        smsCodeService.issueForgotSms(dto.getPhone().trim());
    }

    @Override
    public void forgotPassword(ForgotPasswordDTO dto) {
        smsCodeService.verifyForgotSms(dto.getPhone().trim(), dto.getSmsCode());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null || user.getPhone() == null || !user.getPhone().equals(dto.getPhone())) {
            throw new BusinessException("用户名与手机号不匹配");
        }
        user.setPassword(md5Password(dto.getNewPassword()));
        userMapper.updateById(user);
    }

    private String md5Password(String password) {
        return DigestUtils.md5DigestAsHex(("ecommerce" + password + "salt").getBytes());
    }
}
