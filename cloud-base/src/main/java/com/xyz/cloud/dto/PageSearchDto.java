package com.xyz.cloud.dto;


import lombok.Data;

/**
 * @author sxl
 * @since 2019-12-13 17:33
 */
@Data
public class PageSearchDto {
    /** 页码 从1开始 */
    private int pageNo = 1;
    /** 每页大小 */
    private int pageSize = 20;

}
