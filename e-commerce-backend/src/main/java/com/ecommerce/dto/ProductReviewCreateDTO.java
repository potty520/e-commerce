package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ProductReviewCreateDTO {
    @NotNull(message = "商品不能为空")
    private Long productId;

    private Long orderId;

    @NotNull(message = "评分不能为空")
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 1000, message = "评价内容过长")
    private String content;
}
