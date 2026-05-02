package com.ecommerce.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 预支付/调起支付所需参数（预留：各渠道字段不同，可用 clientParams 承载）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPrepayResponseDTO {
    private Boolean stub;
    private String hint;
    /** 微信 JSAPI 调起示例字段占位 */
    private String prepayId;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String packageValue;
    private String signType;
    private String paySign;
    /** 扩展：支付宝 orderStr 等 */
    private Map<String, String> extra;
}
