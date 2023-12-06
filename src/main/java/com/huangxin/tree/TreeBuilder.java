package com.huangxin.tree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The TreeBuilder class provides a static method to build a tree structure from a list of tree nodes.
 */
public class TreeBuilder {

    public static <T, E extends Tree<T>> List<E> build(List<E> nodes, T rootId) {
        List<E> rootList = nodes.stream().filter(tree -> tree.getParentId().equals(rootId)).collect(Collectors.toList());
        Map<T, T> filterOperated = new HashMap<>(rootList.size() + nodes.size());
        //对每个根节点都封装它的孩子节点
        rootList.forEach(root -> setChildren(root, nodes, filterOperated));
        return rootList;
    }


    private static <T, E extends Tree<T>> void setChildren(Tree<T> root, List<E> nodes, Map<T, T> filterOperated) {
        List<Tree<T>> children = new ArrayList<>();
        nodes.stream()
                //过滤出未操作过的节点
                .filter(body -> !filterOperated.containsKey(body.getId()))
                //过滤出孩子节点
                .filter(body -> Objects.equals(root.getId(), body.getParentId()))
                .forEach(body -> {
                    filterOperated.put(body.getId(), root.getId());
                    children.add(body);
                    //递归 对每个孩子节点执行同样操作
                    setChildren(body, nodes, filterOperated);
                });
        root.setChildren(children);
    }
}