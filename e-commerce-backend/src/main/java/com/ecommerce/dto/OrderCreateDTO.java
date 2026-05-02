package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class OrderCreateDTO {
    private Long addressId;

    private Integer payType;

    private List<CartItemDTO> items;

    @Data
    public static class CartItemDTO {
        private Long productId;
        private Integer quantity;
    }
}
