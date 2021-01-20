package com.xyz.cloud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页Dto
 *
 * @param <T>
 * @author sxl
 * @since 2020-04-16 19:28
 */
@Data
@NoArgsConstructor
public class PageDto<T> {
    /**
     * 总记录数
     */
    private long total;
    /**
     * 总页数
     */
    private long totalPage;
    /**
     * 页码
     */
    private int pageNo = 1;
    /**
     * 每页大小
     */
    private int pageSize;
    /**
     * 本页数据
     */
    private List<T> data;

    public PageDto(long total, long totalPage, int pageNo, int pageSize, List<T> data) {
        this.total = total;
        this.totalPage = totalPage;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.data = data;
    }

    public PageDto(long total, List<T> data, PageSearchDto page) {
        this(total, total % page.getPageSize() == 0 ? total / page.getPageSize() : total / page.getPageSize() + 1,
                page.getPageNo(), page.getPageSize(), data);
    }

    public <R> PageDto(PageDto<R> pageDto, List<T> data) {
        this.data = data;
        this.total = pageDto.getTotal();
        this.totalPage = pageDto.getTotalPage();
        this.pageNo = pageDto.getPageNo();
        this.pageSize = pageDto.getPageSize();
    }
}
