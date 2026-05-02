package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.OrderCreateDTO;
import com.ecommerce.dto.OrderQueryDTO;
import com.ecommerce.dto.RefundApplyDTO;
import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestAttribute("userId") Long userId,
                                                   @Valid @RequestBody OrderCreateDTO dto) {
        Map<String, Object> result = orderService.createOrder(userId, dto);
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<Order> getOrderDetail(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        Order order = orderService.getOrderDetail(userId, id);
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getOrderList(@RequestAttribute("userId") Long userId, OrderQueryDTO dto) {
        Map<String, Object> result = orderService.getOrderList(userId, dto);
        return Result.success(result);
    }

    @GetMapping("/pay/mock-data/{id}")
    public Result<Map<String, Object>> getMockPayData(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        return Result.success(orderService.getMockPayData(userId, id));
    }

    /** 统一下单预支付数据（签名密钥 pay.unified.secret，回调 /api/payment/unified/callback） */
    @GetMapping("/pay/unified-data/{id}")
    public Result<Map<String, Object>> getUnifiedPayData(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        return Result.success(orderService.getUnifiedPrepayData(userId, id));
    }

    @PostMapping("/refund/{id}")
    public Result<Void> applyRefund(@RequestAttribute("userId") Long userId, @PathVariable Long id,
                                    @Valid @RequestBody RefundApplyDTO dto) {
        orderService.applyRefund(userId, id, dto);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancelOrder(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/pay/{id}")
    public Result<Void> payOrder(@RequestAttribute("userId") Long userId, @PathVariable Long id, @RequestParam Integer payType) {
        orderService.payOrder(userId, id, payType);
        return Result.success();
    }

    @PutMapping("/confirm/{id}")
    public Result<Void> confirmReceive(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        orderService.confirmReceive(userId, id);
        return Result.success();
    }
}
