package com.xyz.cloud.sample.controller;

import com.google.common.collect.Lists;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.controller.dto.UserDto;
import com.xyz.desensitize.annotation.Desensitize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sxl
 * @since 2020/9/15 14:06
 */
@Slf4j
@RestController
public class DesensitizeController {

    @Desensitize
    @PostMapping("/desensitize")
    public UserDto desensitize() {
        return buildBaseUser();
    }

    @Desensitize
    @PostMapping("/desensitize1")
    public ResultDto<UserDto> desensitize1() {
        return ResultDto.ok(buildBaseUser());
    }

    @Desensitize
    @PostMapping("/desensitize2")
    public List<UserDto> desensitize2() {
        UserDto userDto = buildBaseUser();
        UserDto userDto1 = buildBaseUser();
        return Lists.newArrayList(userDto1, userDto);
    }

    @Desensitize
    @PostMapping("/desensitize3")
    public ResultDto<List<UserDto>> desensitize3() {
        List<UserDto> userDtos = desensitize2();
        return ResultDto.ok(userDtos);
    }

    @PostMapping("/desensitize4")
    public ResultDto<List<UserDto>> desensitize4() {
        List<UserDto> userDtos = desensitize2();
        return ResultDto.ok(userDtos);
    }

    private UserDto buildBaseUser() {
        UserDto userDto = new UserDto();
        userDto.setName("石秀来");
        userDto.setPassword("11111111111111");
        userDto.setIdCard("61252567384384473847384");
        userDto.setEmail("shixiulai@51zy.com");
        userDto.setPhone("13000000000");
        return userDto;
    }

}
