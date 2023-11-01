package com.huangxin.tree;

import lombok.Data;

import java.util.List;

/**
 * Goods
 *
 * @author 黄鑫
 */
@Data
public class Goods extends Tree<Long> {
    private String name;
}
