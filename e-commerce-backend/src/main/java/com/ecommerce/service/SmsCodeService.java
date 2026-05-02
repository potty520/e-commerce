package com.ecommerce.service;

/**
 * 短信验证码（登录 / 忘记密码），存 Redis，详见 docs/短信服务对接.md。
 */
public interface SmsCodeService {

    void issueLoginSms(String phone);

    void verifyLoginSms(String phone, String smsCode);

    void issueForgotSms(String phone);

    void verifyForgotSms(String phone, String smsCode);
}
