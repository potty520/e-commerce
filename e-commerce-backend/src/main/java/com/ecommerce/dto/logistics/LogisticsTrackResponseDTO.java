package com.ecommerce.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 物流查询响应（当前为预留实现：仅返回订单内已有单号，轨迹列表为空）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsTrackResponseDTO {
    private String orderNo;
    private String logisticsCompany;
    private String logisticsNo;
    /** 轨迹明细；未对接三方前为空列表 */
    private List<LogisticsTraceItemDTO> traces;
    /** true 表示仍为占位/预留逻辑 */
    private Boolean stub;
}
