package com.huangxin.dict;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * DictHandler
 *
 * @author 黄鑫
 */
public interface DictHandler {

    default Map<String, String> handle(Dict dict) {
        Map<String, String> result = null;
        if (dict.cache() && StrUtil.isNotEmpty(dict.key())) {
            result = cacheHandle(dict);
        }
        if (ObjectUtil.isEmpty(result)) {
            result = codeHandle(dict);
        }
        if (ObjectUtil.isEmpty(result)) {
            result = replaceHandle(dict);
        }
        return result;
    }

    Map<String, String> cacheHandle(Dict dict);

    Map<String, String> codeHandle(Dict dict);

    default Map<String, String> replaceHandle(Dict dict) {
        String[] replace = dict.replace();
        if (ArrayUtil.isEmpty(replace)) {
            return null;
        }
        Map<String, String> map = MapUtil.newHashMap(replace.length);
        for (String s : replace) {
            int index = s.indexOf("_");
            String key = s.substring(0, index);
            String value = s.substring(index + 1);
            map.put(key, value);
        }
        return map;
    }
}
