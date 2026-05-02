package com.ecommerce.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付状态查询（预留：主动查单补偿用）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponseDTO {
    private Boolean stub;
    private String hint;
    private String orderNo;
    private String tradeState;
    private String thirdPartyTradeNo;
}
