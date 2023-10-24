package com.huangxin.cache;

import java.util.Collection;
import java.util.Set;

/**
 * 缓存接口
 *
 * @author 黄鑫
 */
public interface CacheService {

    /**
     * 匹配keys
     */
    Set<String> keys(String key);

    Boolean delete(Collection<String> keys);
}
