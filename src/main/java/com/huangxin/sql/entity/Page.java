package com.huangxin.sql.entity;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Page
 *
 * @author 黄鑫
 */
@Data
public class Page<T> {

    /**
     * 当前页
     */
    protected long current = 1;
    /**
     * 每页显示条数，默认 10
     */
    protected long size = 10;
    /**
     * 总数
     */
    protected long total = 0;
    /**
     * 查询数据列表
     */
    protected List<T> records = Collections.emptyList();

    public Page() {
    }

    public Page(long current, long size) {
        this.current = current;
        this.size = size;
    }

    public Page(long current, long size, long total) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
    }
}
