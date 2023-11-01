package com.huangxin.tree;

import lombok.Data;

import java.util.List;

/**
 * 树形结构.
 */
@Data
public class Tree<T> {

  /**
   * id
   */
  private T id;
  /**
   * 父节点id
   */
  private T parentId;
  /**
   * 子节点列表
   */
  private List<? extends Tree<T>> children;

}