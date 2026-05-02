package com.ecommerce.dto;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private String keyword;
    private Long categoryId;
    private String sort;
    private Integer page = 1;
    private Integer pageSize = 10;
}
