package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ShipOrderDTO {
    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;

    @NotBlank(message = "运单号不能为空")
    private String logisticsNo;
}
