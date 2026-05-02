package com.ecommerce.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.ecommerce.common.BusinessException;
import com.ecommerce.dto.CaptchaVO;
import com.ecommerce.service.CaptchaService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final int TTL_MS = 5 * 60 * 1000;

    private static class Holder {
        String codeLower;
        long expireAt;
    }

    private final Map<String, Holder> store = new ConcurrentHashMap<>();

    @Override
    public CaptchaVO generate() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(130, 48, 4, 8);
        String code = captcha.getCode();
        String id = UUID.randomUUID().toString().replace("-", "");
        Holder h = new Holder();
        h.codeLower = code == null ? "" : code.trim().toLowerCase();
        h.expireAt = System.currentTimeMillis() + TTL_MS;
        store.put(id, h);
        return new CaptchaVO(id, captcha.getImageBase64());
    }

    @Override
    public void verifyAndConsume(String captchaId, String captchaCode) {
        if (captchaId == null || captchaCode == null) {
            throw new BusinessException("图形验证码无效");
        }
        String id = captchaId.trim();
        Holder h = store.get(id);
        if (h == null) {
            throw new BusinessException("图形验证码已失效，请重新获取");
        }
        if (System.currentTimeMillis() > h.expireAt) {
            store.remove(id);
            throw new BusinessException("图形验证码已过期，请重新获取");
        }
        String input = captchaCode.trim().toLowerCase();
        if (!h.codeLower.equals(input)) {
            throw new BusinessException("图形验证码错误");
        }
        store.remove(id);
    }
}
