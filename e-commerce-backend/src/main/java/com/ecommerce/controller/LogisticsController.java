package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.logistics.LogisticsTrackResponseDTO;
import com.ecommerce.dto.logistics.LogisticsTraceItemDTO;
import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 物流查询（预留）：路由与响应结构已固定，对接三方物流 API 时在 Service 层实现查轨迹逻辑。
 */
@RestController
@RequestMapping("/api/logistics")
public class LogisticsController {

    @Autowired
    private OrderService orderService;

    /**
     * 按订单查询物流概要（当前仅回传订单表中的承运商、运单号；轨迹 traces 为空）。
     */
    @GetMapping("/order/{orderId}")
    public Result<LogisticsTrackResponseDTO> trackByOrder(@RequestAttribute("userId") Long userId,
                                                          @PathVariable Long orderId) {
        Order order = orderService.getOrderDetail(userId, orderId);
        List<LogisticsTraceItemDTO> traces = Collections.emptyList();
        LogisticsTrackResponseDTO body = LogisticsTrackResponseDTO.builder()
                .orderNo(order.getOrderNo())
                .logisticsCompany(order.getLogisticsCompany())
                .logisticsNo(order.getLogisticsNo())
                .traces(traces)
                .stub(true)
                .build();
        return Result.success(body);
    }

    /**
     * 按承运商编码 + 运单号查询（预留）：后续接入快递 100 等时在此实现，与订单解耦。
     */
    @GetMapping("/query")
    public Result<LogisticsTrackResponseDTO> trackByNumber(@RequestParam String companyCode,
                                                         @RequestParam String trackingNo) {
        LogisticsTrackResponseDTO body = LogisticsTrackResponseDTO.builder()
                .orderNo(null)
                .logisticsCompany(companyCode)
                .logisticsNo(trackingNo)
                .traces(Collections.emptyList())
                .stub(true)
                .build();
        return Result.success(body);
    }
}
