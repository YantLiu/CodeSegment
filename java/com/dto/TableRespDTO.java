package com.alt.base.common.dto.response;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuyanting
 * @description
 * @date: 2018/12/28
 */
@Data
public class TableRespDTO<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;
    /*
     * 当前页码
     */
    private int current = 1;
    /*
     * 每页行数
     */
    private int size = 10;
    /*
     * 总页数
     */
    private int pages = 1;
    /*
     * 数据总行数
     */
    private long total = 0;
    /*
     * 记录列表
     */
    private List<T> records;

    public TableRespDTO(Page page, List<T> list){
        count(page.getPageNum(), page.getPageSize(),  page.getTotal());
        this.records = list;
    }

    public TableRespDTO(){}

    public void count(int current, int size, long total) {
        this.current =current;
        this.size = size;
        this.total = total;
        this.pages  = (int) (total / size) + 1;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}