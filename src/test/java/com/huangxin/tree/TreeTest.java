package com.huangxin.tree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * testTree
 *
 * @author 黄鑫
 */
public class TreeTest {

    @Test
    public void testTree() {
        List<Tree<Integer>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Tree<Integer> tree = new Tree<>();
            tree.setId(i + 1);
            tree.setParentId(i);
            list.add(tree);
        }
        List<Tree<Integer>> build = TreeBuilder.build(list, 0);
        System.out.println(build);
    }
}
