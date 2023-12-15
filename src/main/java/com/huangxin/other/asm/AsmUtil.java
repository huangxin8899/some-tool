package com.huangxin.other.asm;

import cn.hutool.core.util.StrUtil;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.huangxin.other.ClassesFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AsmHandler
 *
 * @author 黄鑫
 */
@Slf4j
@Component
public class AsmUtil implements SmartInitializingSingleton {

    /**
     * 扫描的路径
     */
    private String path = "com.huangxin";

    /**
     * 持久化标识类反射集合
     */
    private static Map<Class<?>, MethodAccess> BEAN_INVOKE;

    /**
     * 获取属性方法前缀
     */
    private static final String GET = "get{}";

    /**
     * 设置属性方法前缀
     */
    private static final String SET = "set{}";

    @Override
    public void afterSingletonsInstantiated() {
        if (StrUtil.isEmpty(path)) {
            log.info("路径为空, {}未进行初始化", this.getClass().getSimpleName());
            return;
        }
        List<Class<?>> classes = ClassesFilter.getClassesFilterForAnnotation(path, null, Collections.singletonList(Asm.class), null);
        BEAN_INVOKE = new ConcurrentHashMap<>(classes.size());
        for (Class<?> item : classes) {
            BEAN_INVOKE.put(item, MethodAccess.get(item));
        }
    }

    /**
     * 根据字段获取值
     *
     * @param o         对象
     * @param fieldName 字段名称
     * @return          值对象
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T get(Object o, String fieldName) {
        return (T) invoke(o, StrUtil.format(GET, StrUtil.upperFirst(fieldName)));
    }

    /**
     * 根据字段设置值
     *
     * @param o         对象
     * @param fieldName 字段名称
     */
    public static void set(Object o, String fieldName, Object value) {
        invoke(o, StrUtil.format(SET, StrUtil.upperFirst(fieldName)), value);
    }

    /**
     * 执行方法
     * @param o             对象
     * @param methodName    方法名称
     * @param args          方法参数
     * @return              返回值
     */
    public static Object invoke(Object o, String methodName, Object... args) {
        Class<?> type = o.getClass();
        MethodAccess methodAccess = BEAN_INVOKE.get(type);
        if (methodAccess == null) {
            methodAccess = MethodAccess.get(type);
            BEAN_INVOKE.put(type, methodAccess);
        }
        return methodAccess.invoke(o, methodName, args);
    }
}
