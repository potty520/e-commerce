package com.ecommerce.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流轨迹节点（预留：对接快递 100 / 菜鸟 / 顺丰等后填充）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsTraceItemDTO {
    private String time;
    private String status;
    private String description;
}
