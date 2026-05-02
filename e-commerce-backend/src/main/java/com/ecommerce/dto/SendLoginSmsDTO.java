package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SendLoginSmsDTO {
    @NotBlank(message = "请输入手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "请先获取图形验证码")
    private String captchaId;

    @NotBlank(message = "请输入图形验证码")
    private String captchaCode;
}
