package com.ecommerce.service;

import com.ecommerce.dto.CaptchaVO;

public interface CaptchaService {

    CaptchaVO generate();

    /**
     * 校验图形验证码（不区分大小写），通过后删除该 captchaId，防止重复使用。
     */
    void verifyAndConsume(String captchaId, String captchaCode);
}
