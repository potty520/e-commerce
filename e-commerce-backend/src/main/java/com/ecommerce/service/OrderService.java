package com.ecommerce.service;

import com.ecommerce.dto.AdminOrderCloseDTO;
import com.ecommerce.dto.AdminOrderRemarkDTO;
import com.ecommerce.dto.AdminOrderQueryDTO;
import com.ecommerce.dto.MockPayCallbackDTO;
import com.ecommerce.dto.OrderCreateDTO;
import com.ecommerce.dto.OrderQueryDTO;
import com.ecommerce.dto.RefundApplyDTO;
import com.ecommerce.dto.RefundRejectDTO;
import com.ecommerce.dto.ShipOrderDTO;

import com.ecommerce.entity.Order;
import java.util.Map;

public interface OrderService {
    Map<String, Object> createOrder(Long userId, OrderCreateDTO dto);
    Order getOrderDetail(Long userId, Long orderId);
    Map<String, Object> getOrderList(Long userId, OrderQueryDTO dto);
    void cancelOrder(Long userId, Long orderId);
    void payOrder(Long userId, Long orderId, Integer payType);
    void confirmReceive(Long userId, Long orderId);

    Map<String, Object> adminOrderList(AdminOrderQueryDTO dto);

    void adminShipOrder(Long orderId, ShipOrderDTO dto);

    void applyRefund(Long userId, Long orderId, RefundApplyDTO dto);

    void adminApproveRefund(Long orderId);

    void adminRejectRefund(Long orderId, RefundRejectDTO dto);

    void adminUpdateOrderRemark(Long orderId, AdminOrderRemarkDTO dto);

    void adminCloseOrder(Long orderId, AdminOrderCloseDTO dto);

    Map<String, Object> getMockPayData(Long userId, Long orderId);

    void handleMockPayCallback(MockPayCallbackDTO dto);

    /** 统一下单预支付数据（与沙箱 mock 并行，签名密钥不同） */
    Map<String, Object> getUnifiedPrepayData(Long userId, Long orderId);

    void handleUnifiedPayCallback(MockPayCallbackDTO dto);
}
