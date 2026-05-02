package com.ecommerce.controller.admin;

import com.ecommerce.common.Result;
import com.ecommerce.dto.AdminOrderCloseDTO;
import com.ecommerce.dto.AdminOrderQueryDTO;
import com.ecommerce.dto.AdminOrderRemarkDTO;
import com.ecommerce.dto.RefundRejectDTO;
import com.ecommerce.dto.ShipOrderDTO;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<Map<String, Object>> list(AdminOrderQueryDTO dto) {
        return Result.success(orderService.adminOrderList(dto));
    }

    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id, @Valid @RequestBody ShipOrderDTO dto) {
        orderService.adminShipOrder(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/refund/approve")
    public Result<Void> approveRefund(@PathVariable Long id) {
        orderService.adminApproveRefund(id);
        return Result.success();
    }

    @PutMapping("/{id}/refund/reject")
    public Result<Void> rejectRefund(@PathVariable Long id, @Valid @RequestBody RefundRejectDTO dto) {
        orderService.adminRejectRefund(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/remark")
    public Result<Void> updateRemark(@PathVariable Long id, @Valid @RequestBody AdminOrderRemarkDTO dto) {
        orderService.adminUpdateOrderRemark(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/close")
    public Result<Void> closeOrder(@PathVariable Long id, @RequestBody(required = false) AdminOrderCloseDTO dto) {
        orderService.adminCloseOrder(id, dto == null ? new AdminOrderCloseDTO() : dto);
        return Result.success();
    }
}
