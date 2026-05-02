package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.MockPayCallbackDTO;
import com.ecommerce.dto.payment.PaymentPrepayRequestDTO;
import com.ecommerce.dto.payment.PaymentPrepayResponseDTO;
import com.ecommerce.dto.payment.PaymentStatusResponseDTO;
import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付网关统一入口（预留）。与 {@link PayController} 中「沙箱回调」并存：
 * <ul>
 *   <li>本类：预支付创建、查单、异步通知占位 —— 对接微信/支付宝正式网关时使用</li>
 *   <li>/api/pay/mock/callback：当前可用的模拟支付回调</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentGatewayController {

    @Autowired
    private OrderService orderService;

    @Value("${pay.unified.enabled:true}")
    private boolean payUnifiedEnabled;

    /**
     * 创建预支付：若 pay.unified.enabled=true，返回本系统统一下单参数（与沙箱 mock 密钥不同）；否则返回占位 stub。
     */
    @PostMapping("/prepay")
    public Result<PaymentPrepayResponseDTO> prepay(@RequestAttribute("userId") Long userId,
                                                     @Valid @RequestBody PaymentPrepayRequestDTO dto) {
        Order order = orderService.getOrderDetail(userId, dto.getOrderId());
        if (payUnifiedEnabled) {
            Map<String, Object> u = orderService.getUnifiedPrepayData(userId, dto.getOrderId());
            Map<String, String> extra = new HashMap<>();
            extra.put("orderNo", String.valueOf(u.get("orderNo")));
            extra.put("payTradeNo", String.valueOf(u.get("payTradeNo")));
            extra.put("sign", String.valueOf(u.get("sign")));
            extra.put("payType", String.valueOf(u.get("payType")));
            extra.put("callbackPath", "/payment/unified/callback");
            PaymentPrepayResponseDTO body = PaymentPrepayResponseDTO.builder()
                    .stub(false)
                    .hint("统一下单（演示）：请用返回参数 POST /api/payment/unified/callback 完成支付，签名为 MD5(orderNo+payTradeNo+pay.unified.secret)")
                    .prepayId(String.valueOf(u.get("payTradeNo")))
                    .appId(null)
                    .timeStamp(null)
                    .nonceStr(null)
                    .packageValue(null)
                    .signType("MD5")
                    .paySign(String.valueOf(u.get("sign")))
                    .extra(extra)
                    .build();
            return Result.success(body);
        }
        PaymentPrepayResponseDTO body = PaymentPrepayResponseDTO.builder()
                .stub(true)
                .hint("预留接口：订单 " + order.getOrderNo() + " 请接入微信/支付宝统一下单后填充 prepayId、paySign 等字段")
                .prepayId(null)
                .appId(null)
                .timeStamp(null)
                .nonceStr(null)
                .packageValue(null)
                .signType(null)
                .paySign(null)
                .extra(null)
                .build();
        return Result.success(body);
    }

    /**
     * 统一下单异步回调（与 /api/pay/mock/callback 并行，验签密钥为 pay.unified.secret）。
     */
    @PostMapping("/unified/callback")
    public Result<Void> unifiedCallback(@Valid @RequestBody MockPayCallbackDTO dto) {
        orderService.handleUnifiedPayCallback(dto);
        return Result.success();
    }

    /**
     * 查询支付状态（预留）：用于前端轮询或补单，对接后改为查微信/支付宝交易状态。
     */
    @GetMapping("/status")
    public Result<PaymentStatusResponseDTO> status(@RequestAttribute("userId") Long userId,
                                                   @RequestParam Long orderId) {
        Order order = orderService.getOrderDetail(userId, orderId);
        String tradeState = order.getPayTime() != null ? "SUCCESS" : "NOTPAY";
        PaymentStatusResponseDTO body = PaymentStatusResponseDTO.builder()
                .stub(true)
                .hint("预留接口：tradeState 当前由本库订单推导，非渠道真实状态")
                .orderNo(order.getOrderNo())
                .tradeState(tradeState)
                .thirdPartyTradeNo(order.getPayTradeNo())
                .build();
        return Result.success(body);
    }

    /**
     * 支付宝异步通知占位（无需登录）。接入时验签、幂等更新订单。
     */
    @PostMapping("/notify/alipay")
    public String notifyAlipayPlaceholder() {
        return "success";
    }

    /**
     * 微信支付异步通知占位（无需登录）。接入时解析 XML/JSON、验签、幂等更新订单。
     */
    @PostMapping(value = "/notify/wechat", produces = MediaType.APPLICATION_XML_VALUE)
    public String notifyWechatPlaceholder() {
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
}
