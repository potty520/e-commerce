package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Constants;
import com.ecommerce.dto.*;
import com.ecommerce.entity.*;
import com.ecommerce.mapper.*;
import com.ecommerce.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Value("${pay.mock.secret}")
    private String mockPaySecret;

    @Value("${pay.unified.secret:ecommerce-unified-pay-secret-2024}")
    private String unifiedPaySecret;

    @Override
    @Transactional
    public Map<String, Object> createOrder(Long userId, OrderCreateDTO dto) {
        List<OrderCreateDTO.CartItemDTO> items = dto.getItems();
        if (items == null || items.isEmpty()) {
            throw new BusinessException("Please select items to order");
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (OrderCreateDTO.CartItemDTO item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || product.getStatus().equals(Constants.ProductStatus.OFF_SALE)) {
                throw new BusinessException("Product not found or off sale: " + item.getProductId());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock: " + product.getName());
            }
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("productId", product.getId());
            orderItem.put("productName", product.getName());
            orderItem.put("productImage", product.getMainImage());
            orderItem.put("unitPrice", product.getPrice());
            orderItem.put("quantity", item.getQuantity());
            orderItem.put("totalPrice", itemTotal);
            orderItems.add(orderItem);
            product.setStock(product.getStock() - item.getQuantity());
            product.setSales(product.getSales() + item.getQuantity());
            productMapper.updateById(product);
            if (dto.getAddressId() == null) {
                LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Cart::getUserId, userId).eq(Cart::getProductId, item.getProductId());
                cartMapper.delete(wrapper);
            }
        }
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(dto.getAddressId());
        order.setTotalAmount(totalAmount);
        order.setStatus(Constants.OrderStatus.PENDING_PAYMENT);
        order.setPayType(dto.getPayType());
        orderMapper.insert(order);
        for (Map<String, Object> item : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setUserId(userId);
            orderItem.setProductId((Long) item.get("productId"));
            orderItem.setProductName((String) item.get("productName"));
            orderItem.setProductImage((String) item.get("productImage"));
            orderItem.setUnitPrice((BigDecimal) item.get("unitPrice"));
            orderItem.setQuantity((Integer) item.get("quantity"));
            orderItem.setTotalPrice((BigDecimal) item.get("totalPrice"));
            orderItemMapper.insert(orderItem);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("totalAmount", totalAmount);
        return result;
    }

    @Override
    public Order getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("Order not found");
        }
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        order.setOrderItems(orderItemMapper.selectList(wrapper));
        return order;
    }

    @Override
    public Map<String, Object> getOrderList(Long userId, OrderQueryDTO dto) {
        int page = dto.getPage() == null || dto.getPage() < 1 ? 1 : dto.getPage();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();
        pageSize = Math.min(pageSize, 100);

        LambdaQueryWrapper<Order> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Order::getUserId, userId);
        if (dto.getStatus() != null) {
            countWrapper.eq(Order::getStatus, dto.getStatus());
        }
        long total = orderMapper.selectCount(countWrapper);

        LambdaQueryWrapper<Order> listWrapper = new LambdaQueryWrapper<>();
        listWrapper.eq(Order::getUserId, userId);
        if (dto.getStatus() != null) {
            listWrapper.eq(Order::getStatus, dto.getStatus());
        }
        listWrapper.orderByDesc(Order::getId);
        int offset = (page - 1) * pageSize;
        listWrapper.last("LIMIT " + offset + "," + pageSize);

        List<Order> records = orderMapper.selectList(listWrapper);
        for (Order order : records) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            order.setOrderItems(orderItemMapper.selectList(itemWrapper));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", records);
        map.put("total", total);
        map.put("page", (long) page);
        map.put("pageSize", (long) pageSize);
        return map;
    }

    @Override
    public Map<String, Object> adminOrderList(AdminOrderQueryDTO dto) {
        int page = dto.getPage() == null || dto.getPage() < 1 ? 1 : dto.getPage();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();
        pageSize = Math.min(pageSize, 100);

        LambdaQueryWrapper<Order> countWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null) {
            countWrapper.eq(Order::getStatus, dto.getStatus());
        }
        if (dto.getUserId() != null) {
            countWrapper.eq(Order::getUserId, dto.getUserId());
        }
        if (StringUtils.hasText(dto.getOrderNo())) {
            countWrapper.like(Order::getOrderNo, dto.getOrderNo());
        }
        countWrapper.orderByDesc(Order::getId);
        long total = orderMapper.selectCount(countWrapper);

        LambdaQueryWrapper<Order> listWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null) {
            listWrapper.eq(Order::getStatus, dto.getStatus());
        }
        if (dto.getUserId() != null) {
            listWrapper.eq(Order::getUserId, dto.getUserId());
        }
        if (StringUtils.hasText(dto.getOrderNo())) {
            listWrapper.like(Order::getOrderNo, dto.getOrderNo());
        }
        listWrapper.orderByDesc(Order::getId);
        int offset = (page - 1) * pageSize;
        listWrapper.last("LIMIT " + offset + "," + pageSize);

        List<Order> records = orderMapper.selectList(listWrapper);
        for (Order order : records) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            order.setOrderItems(orderItemMapper.selectList(itemWrapper));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", records);
        map.put("total", total);
        map.put("page", (long) page);
        map.put("pageSize", (long) pageSize);
        return map;
    }

    @Override
    public void adminShipOrder(Long orderId, ShipOrderDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.PAID)) {
            throw new BusinessException("仅已付款订单可发货");
        }
        order.setStatus(Constants.OrderStatus.SHIPPED);
        order.setLogisticsCompany(dto.getLogisticsCompany());
        order.setLogisticsNo(dto.getLogisticsNo());
        order.setDeliverTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void applyRefund(Long userId, Long orderId, RefundApplyDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        Integer s = order.getStatus();
        if (!(s.equals(Constants.OrderStatus.PAID)
                || s.equals(Constants.OrderStatus.SHIPPED)
                || s.equals(Constants.OrderStatus.RECEIVED))) {
            throw new BusinessException("当前状态不可申请退款");
        }
        order.setStatusBeforeRefund(s);
        order.setRefundReason(dto.getReason());
        order.setStatus(Constants.OrderStatus.REFUNDING);
        orderMapper.updateById(order);
    }

    @Override
    public void adminApproveRefund(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.REFUNDING)) {
            throw new BusinessException("仅退款中订单可审核通过");
        }
        order.setStatus(Constants.OrderStatus.REFUNDED);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void adminRejectRefund(Long orderId, RefundRejectDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.REFUNDING)) {
            throw new BusinessException("仅退款中订单可拒绝");
        }
        Integer prev = order.getStatusBeforeRefund();
        if (prev == null) {
            throw new BusinessException("数据异常：缺少退款前状态");
        }
        order.setStatus(prev);
        order.setRefundRejectReason(dto.getReason());
        order.setStatusBeforeRefund(null);
        orderMapper.updateById(order);
    }

    @Override
    public void adminUpdateOrderRemark(Long orderId, AdminOrderRemarkDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setAdminRemark(dto.getAdminRemark());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void adminCloseOrder(Long orderId, AdminOrderCloseDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        Integer s = order.getStatus();
        if (!(s.equals(Constants.OrderStatus.PENDING_PAYMENT) || s.equals(Constants.OrderStatus.PAID))) {
            throw new BusinessException("仅待付款或已付款（未发货）订单可由后台关闭");
        }
        if (s.equals(Constants.OrderStatus.PAID)) {
            if (order.getDeliverTime() != null) {
                throw new BusinessException("已发货订单请走售后流程，不能直接关闭");
            }
        }
        restoreStockForOrder(orderId);
        order.setStatus(Constants.OrderStatus.CLOSED);
        order.setCloseTime(LocalDateTime.now());
        if (StringUtils.hasText(dto.getReason())) {
            String prefix = StringUtils.hasText(order.getAdminRemark()) ? order.getAdminRemark() + " | " : "";
            order.setAdminRemark(prefix + "[关闭] " + dto.getReason());
        }
        orderMapper.updateById(order);
    }

    private void restoreStockForOrder(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productMapper.updateById(product);
            }
        }
    }

    @Override
    public Map<String, Object> getMockPayData(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.PENDING_PAYMENT)) {
            throw new BusinessException("仅待付款订单可发起沙箱支付");
        }
        String payTradeNo = "MOCK" + System.currentTimeMillis();
        String sign = DigestUtils.md5DigestAsHex(
                (order.getOrderNo() + payTradeNo + mockPaySecret).getBytes(StandardCharsets.UTF_8));
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", order.getOrderNo());
        map.put("payTradeNo", payTradeNo);
        map.put("sign", sign);
        map.put("payType", 2);
        return map;
    }

    @Override
    @Transactional
    public void handleMockPayCallback(MockPayCallbackDTO dto) {
        String expect = DigestUtils.md5DigestAsHex(
                (dto.getOrderNo() + dto.getPayTradeNo() + mockPaySecret).getBytes(StandardCharsets.UTF_8));
        if (!expect.equalsIgnoreCase(dto.getSign())) {
            throw new BusinessException("签名校验失败");
        }
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, dto.getOrderNo());
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus().equals(Constants.OrderStatus.PAID)) {
            if (dto.getPayTradeNo().equals(order.getPayTradeNo())) {
                return;
            }
            throw new BusinessException("订单已支付");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.PENDING_PAYMENT)) {
            throw new BusinessException("订单状态不可支付");
        }
        order.setStatus(Constants.OrderStatus.PAID);
        order.setPayType(dto.getPayType() == null ? 2 : dto.getPayType());
        order.setPayTradeNo(dto.getPayTradeNo());
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public Map<String, Object> getUnifiedPrepayData(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.PENDING_PAYMENT)) {
            throw new BusinessException("仅待付款订单可发起支付");
        }
        String payTradeNo = "U" + System.currentTimeMillis();
        String sign = DigestUtils.md5DigestAsHex(
                (order.getOrderNo() + payTradeNo + unifiedPaySecret).getBytes(StandardCharsets.UTF_8));
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", order.getOrderNo());
        map.put("payTradeNo", payTradeNo);
        map.put("sign", sign);
        map.put("payType", 2);
        map.put("channel", "UNIFIED");
        return map;
    }

    @Override
    @Transactional
    public void handleUnifiedPayCallback(MockPayCallbackDTO dto) {
        String expect = DigestUtils.md5DigestAsHex(
                (dto.getOrderNo() + dto.getPayTradeNo() + unifiedPaySecret).getBytes(StandardCharsets.UTF_8));
        if (!expect.equalsIgnoreCase(dto.getSign())) {
            throw new BusinessException("签名校验失败");
        }
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, dto.getOrderNo());
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus().equals(Constants.OrderStatus.PAID)) {
            if (dto.getPayTradeNo().equals(order.getPayTradeNo())) {
                return;
            }
            throw new BusinessException("订单已支付");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.PENDING_PAYMENT)) {
            throw new BusinessException("订单状态不可支付");
        }
        order.setStatus(Constants.OrderStatus.PAID);
        order.setPayType(dto.getPayType() == null ? 2 : dto.getPayType());
        order.setPayTradeNo(dto.getPayTradeNo());
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("Order not found");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.PENDING_PAYMENT)) {
            throw new BusinessException("Only pending payment orders can be cancelled");
        }
        restoreStockForOrder(orderId);
        order.setStatus(Constants.OrderStatus.CLOSED);
        order.setCloseTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void payOrder(Long userId, Long orderId, Integer payType) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("Order not found");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.PENDING_PAYMENT)) {
            throw new BusinessException("Order cannot be paid");
        }
        order.setStatus(Constants.OrderStatus.PAID);
        order.setPayType(payType);
        order.setPayTradeNo("LOCAL" + System.currentTimeMillis());
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void confirmReceive(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("Order not found");
        }
        if (!order.getStatus().equals(Constants.OrderStatus.SHIPPED)) {
            throw new BusinessException("Order cannot be confirmed");
        }
        order.setStatus(Constants.OrderStatus.RECEIVED);
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        return System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
    }
}
