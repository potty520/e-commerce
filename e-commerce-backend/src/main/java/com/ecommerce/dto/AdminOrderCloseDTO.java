package com.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class AdminOrderCloseDTO {
    /** 关闭说明，会写入管理员备注（追加） */
    @Size(max = 500, message = "说明过长")
    private String reason;
}
