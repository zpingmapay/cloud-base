package com.xyz.cloud.sample.controller.dto;

import com.xyz.desensitize.annotation.*;
import lombok.Data;

/**
 * @author sxl
 * @since 2020/9/15 14:08
 */
@Data
public class UserDto {

    @DesensitizeChineseName
    private String name;

    @DesensitizeCardId
    private String idCard;

    @DesensitizePassword
    private String password;

    @DesensitizeEmail
    private String email;

    @DesensitizePhone
    private String phone;
}
