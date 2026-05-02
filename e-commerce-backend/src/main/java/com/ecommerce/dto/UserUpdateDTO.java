package com.ecommerce.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String nickname;
    private String email;
    private Integer gender;
    private String birthday;
    private String avatar;
}
