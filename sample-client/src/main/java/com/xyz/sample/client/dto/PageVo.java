package com.xyz.sample.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    /** 总记录数 */
    private Integer total;
    /** 总页数 */
    private Integer totalPage;
    /** 页码 */
    private Integer pageNo;
    /** 每页大小 */
    private Integer pageSize;
    /** 本页数据 */
    private List<T> data;

    public PageVo() {
    }

    public PageVo(Integer total, Integer totalPage, Integer pageNo, Integer pageSize, List<T> data) {
        this.total = total;
        this.totalPage = totalPage;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.data = data;
    }

    public PageVo(Integer total, List<T> data, SearchPage page) {
        this(total, total % page.getPageSize() == 0 ? total / page.getPageSize() : total / page.getPageSize() + 1, page.getPageNo(), page.getPageSize(), data);
    }

    public PageVo(PageVo pageVo, List<T> data) {
        this.data = data;
        this.total = pageVo.getTotal();
        this.totalPage = pageVo.getTotalPage();
        this.pageNo = pageVo.getPageNo();
        this.pageSize = pageVo.getPageSize();
    }
}
