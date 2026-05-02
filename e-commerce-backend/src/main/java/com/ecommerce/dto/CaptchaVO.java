package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {
    private String captchaId;
    /** PNG Base64，不含 data:image 前缀 */
    private String imageBase64;
}
