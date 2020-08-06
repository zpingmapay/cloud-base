package com.xyz.sample.client.dto;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

public class SearchPage {
    /** 页码 从1开始 */
    private Integer pageNo = 1;
    /** 每页大小 */
    private Integer pageSize = 20;

    public SearchPage(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public SearchPage() {
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public <E> Page<E> startPage() {
        return PageHelper.startPage(this.getPageNo(), this.getPageSize());
    }

}
