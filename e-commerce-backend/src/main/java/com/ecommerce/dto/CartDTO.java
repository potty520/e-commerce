package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CartDTO {
    @NotNull(message = "Product ID cannot be empty")
    private Long productId;

    @NotNull(message = "Quantity cannot be empty")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
