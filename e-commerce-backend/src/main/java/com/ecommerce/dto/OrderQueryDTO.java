package com.ecommerce.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
