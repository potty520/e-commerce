package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class AdminOrderRemarkDTO {
    @Size(max = 500, message = "备注过长")
    private String adminRemark;
}
