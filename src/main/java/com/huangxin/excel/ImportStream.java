package com.huangxin.excel;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * ImportStream
 *
 * @author 黄鑫
 */
public interface ImportStream<T> {

    ImportStream<T> filter(Predicate<? super T> predicate);

    ImportStream<T> peek(Consumer<? super T> action);

    ImportStream<T> limit(long maxSize);

    ImportStream<T> skip(long n);

    void forEach(Consumer<? super T> action);

    List<T> toList(Integer sheetNo, Integer headNum);
}
