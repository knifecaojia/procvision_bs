package com.imustsz.cilent.domain.vo;

import com.imustsz.common.core.page.TableDataInfo;

public class TaskConditionVO extends TableDataInfo {
    private Integer page;
    private Integer page_size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer pageNum) {
        this.page = pageNum;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer pageSize) {
        this.page_size = pageSize;
    }
}
