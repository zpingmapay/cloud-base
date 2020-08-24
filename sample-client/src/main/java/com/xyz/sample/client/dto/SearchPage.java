package com.xyz.sample.client.dto;


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

}
