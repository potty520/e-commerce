package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RefundApplyDTO {
    @NotBlank(message = "请填写退款原因")
    private String reason;
}
