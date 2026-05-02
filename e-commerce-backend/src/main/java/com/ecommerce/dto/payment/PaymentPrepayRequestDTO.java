package com.ecommerce.dto.payment;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 创建预支付单请求（预留：对接微信/支付宝统一下单）。
 */
@Data
public class PaymentPrepayRequestDTO {
    @NotNull(message = "orderId 不能为空")
    private Long orderId;

    /**
     * 支付渠道：WECHAT_JSAPI | WECHAT_APP | ALIPAY_APP | ALIPAY_WAP 等（与真实网关对齐时再约束枚举）
     */
    private String channel;
}
