package com.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddressDTO {
    private Long id;

    @NotBlank(message = "Receiver name cannot be empty")
    private String receiverName;

    @NotBlank(message = "Receiver phone cannot be empty")
    private String receiverPhone;

    @NotBlank(message = "Province cannot be empty")
    private String province;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotBlank(message = "District cannot be empty")
    private String district;

    @NotBlank(message = "Detail address cannot be empty")
    private String detailAddress;

    private Integer isDefault;
}
