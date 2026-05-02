package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RefundRejectDTO {
    @NotBlank(message = "请填写拒绝原因")
    @Size(max = 500, message = "原因过长")
    private String reason;
}
