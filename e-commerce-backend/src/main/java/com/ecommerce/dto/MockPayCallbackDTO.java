package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MockPayCallbackDTO {
    @NotBlank
    private String orderNo;

    @NotBlank
    private String payTradeNo;

    /** 1微信 2支付宝 3银行卡 */
    private Integer payType = 2;

    @NotBlank
    private String sign;
}
