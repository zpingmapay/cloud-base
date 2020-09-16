package com.xyz.cloud.sample.controller.dto;

import com.xyz.desensitize.annotation.Desensitized;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ResultDto {

    private int code;

    private String msg;

    @Desensitized
    private UserDto data;

    public ResultDto(int code, String msg, UserDto data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public static ResultDto ok(UserDto data) {
        return new ResultDto(HttpStatus.OK.value(), "OK", data);
    }

}
