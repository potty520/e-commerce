package com.ecommerce.service.impl;

import com.ecommerce.common.BusinessException;
import com.ecommerce.service.SmsCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class RedisSmsCodeServiceImpl implements SmsCodeService {

    private static final Logger log = LoggerFactory.getLogger(RedisSmsCodeServiceImpl.class);
    private static final String P = "ec:sms:";
    private static final int CODE_TTL_MIN = 5;
    private static final int RESEND_SEC = 60;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String loginCodeKey(String phone) {
        return P + "login:code:" + phone;
    }

    private String loginCoolKey(String phone) {
        return P + "login:cool:" + phone;
    }

    private String forgotCodeKey(String phone) {
        return P + "forgot:code:" + phone;
    }

    private String forgotCoolKey(String phone) {
        return P + "forgot:cool:" + phone;
    }

    private String random6() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    @Override
    public void issueLoginSms(String phone) {
        String p = phone.trim();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(loginCoolKey(p)))) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }
        String code = random6();
        stringRedisTemplate.opsForValue().set(loginCodeKey(p), code, CODE_TTL_MIN, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(loginCoolKey(p), "1", RESEND_SEC, TimeUnit.SECONDS);
        log.info("[登录短信] 手机号 {} 验证码 {} （生产请改为调用短信平台 SDK）", p, code);
    }

    @Override
    public void verifyLoginSms(String phone, String smsCode) {
        verifyConsume(loginCodeKey(phone.trim()), smsCode, "请先获取短信验证码", "短信验证码错误");
    }

    @Override
    public void issueForgotSms(String phone) {
        String p = phone.trim();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(forgotCoolKey(p)))) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }
        String code = random6();
        stringRedisTemplate.opsForValue().set(forgotCodeKey(p), code, CODE_TTL_MIN, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(forgotCoolKey(p), "1", RESEND_SEC, TimeUnit.SECONDS);
        log.info("[忘记密码短信] 手机号 {} 验证码 {} （生产请改为调用短信平台 SDK）", p, code);
    }

    @Override
    public void verifyForgotSms(String phone, String smsCode) {
        verifyConsume(forgotCodeKey(phone.trim()), smsCode, "请先获取短信验证码", "短信验证码错误");
    }

    private void verifyConsume(String key, String smsCode, String emptyMsg, String wrongMsg) {
        if (smsCode == null) {
            throw new BusinessException(401, emptyMsg);
        }
        String stored = stringRedisTemplate.opsForValue().get(key);
        if (stored == null) {
            throw new BusinessException(401, emptyMsg);
        }
        if (!stored.equals(smsCode.trim())) {
            throw new BusinessException(401, wrongMsg);
        }
        stringRedisTemplate.delete(key);
    }
}
