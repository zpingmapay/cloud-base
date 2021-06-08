package com.xyz.cloud.dto;


import lombok.Data;

/**
 * @author sxl
 * @since 2019-12-13 17:33
 */
@Data
public class PageSearchDto {
    /** 页码 从1开始 */
    private Integer pageNo = 1;
    /** 每页大小 */
    private Integer pageSize = 20;

}
