package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.MockPayCallbackDTO;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 模拟支付回调。正式环境「统一下单 / 查单」见 {@link PaymentGatewayController}（/api/payment，当前多为预留）。
 */
@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Autowired
    private OrderService orderService;

    /**
     * 沙箱支付异步回调（无需登录）。签名为 MD5(orderNo + payTradeNo + pay.mock.secret) 小写十六进制。
     */
    @PostMapping("/mock/callback")
    public Result<Void> mockCallback(@Valid @RequestBody MockPayCallbackDTO dto) {
        orderService.handleMockPayCallback(dto);
        return Result.success();
    }
}
