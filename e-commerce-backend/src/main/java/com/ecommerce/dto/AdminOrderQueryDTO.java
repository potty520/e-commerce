package com.ecommerce.dto;

import lombok.Data;

@Data
public class AdminOrderQueryDTO {
    private Integer status;
    private Long userId;
    private String orderNo;
    private Integer page = 1;
    private Integer pageSize = 10;
}
