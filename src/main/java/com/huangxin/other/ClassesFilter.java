package com.huangxin.other;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * 类包过滤工具 spring装载bean启动后使用
 * 未收缩窄修饰符，可以公共使用
 * 为了方便使用，所有对外方法静态化，因为加载时机的问题，记得在spring bean工厂完成初始化后使用，防止bean加载顺序导致的空指针
 *
 * @author 黄鑫
 */
@Slf4j
@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class ClassesFilter implements ResourceLoaderAware {

    /**
     * spring匹配器
     */
    private static ResourcePatternResolver RESOLVER;

    /**
     * spring元数据解析器
     */
    private static MetadataReaderFactory REGISTER;

    /**
     * 环境对象
     */
    private static final StandardEnvironment ENVIRONMENT = new StandardEnvironment();

    /**
     * 路径匹配正则
     */
    private static final String CLASSES_RESOURCE_PATH_PREFIX = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "%s" + "/**/*.class";

    /**
     * 注解断言
     */
    private static final BiPredicate<Class, Class> FILTER_FOR_ANNOTATION = (aClass1, annotationClass) -> annotationClass.isAnnotationPresent(aClass1);

    /**
     * 接口断言
     */
    private static final BiPredicate<Class, Class> FILTER_FOR_INTERFACE_OR_CLASS = Class::isAssignableFrom;

    /**
     * 默认无断言
     */
    private static final BiPredicate<Class, Class> FILTER_FOR_NOTHING = (clazz, s) -> true;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        RESOLVER = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        REGISTER = new CachingMetadataReaderFactory(resourceLoader);
    }

    /**
     * 根据包路径,获取Class的资源路径
     *
     * @param packagePath 类包路径 classes路径
     * @return 路径
     */
    private static String getResourcePath(String packagePath) {
        Assert.isTrue(StrUtil.isNotBlank(packagePath), "packagePath为null");
        return String.format(CLASSES_RESOURCE_PATH_PREFIX, ClassUtils.convertClassNameToResourcePath(ENVIRONMENT.resolveRequiredPlaceholders(packagePath)));
    }

    /**
     * 获取指定路径下的资源信息
     *
     * @param pkgPath 类包路径 classes路径
     * @return 资源集合
     */
    @SneakyThrows
    private static List<Resource> getResource(String pkgPath, String... regular) {
        Resource[] resources = RESOLVER.getResources(getResourcePath(pkgPath));
        if (Objects.isNull(regular) || 0 == regular.length || regular[0] == null) {
            return Arrays.asList(resources);
        }

        Pattern p;
        List<Resource> resourceResults = new ArrayList<>(resources.length);
        for (String s : regular) {
            p = Pattern.compile(s);
            for (Resource resource : resources) {
                if (p.matcher(resource.getURL().getPath()).find()) {
                    resourceResults.add(resource);
                }
            }
        }

        return resourceResults;
    }

    /**
     * 注解过滤
     *
     * @param pkgPath        类包路径
     * @param annotationList 注解类
     * @param regular        绝对路径过滤器 支持正则
     * @return 类集合
     */
    public static List<Class<?>> getClassesFilterForAnnotation(String pkgPath, ClassLoader loader, List<Class> annotationList, String regular) {
        return doGetClasses(pkgPath, loader, FILTER_FOR_ANNOTATION, annotationList, regular);
    }

    /**
     * 接口过滤
     *
     * @param pkgPath            类包路径
     * @param interfaceClassList 注解类
     * @param regular            绝对路径过滤器 支持正则
     * @return 类集合
     */
    public static List<Class<?>> getClassesFilterForInterface(String pkgPath, ClassLoader loader, List<Class> interfaceClassList, String... regular) {
        return doGetClasses(pkgPath, loader, FILTER_FOR_INTERFACE_OR_CLASS, interfaceClassList, regular);
    }

    /**
     * 接口过滤
     *
     * @param pkgPath   类包路径
     * @param classList 注解类
     * @param regular   绝对路径过滤器 支持正则
     * @return 类集合
     */
    public static List<Class<?>> getClassesFilterForClass(String pkgPath, ClassLoader loader, List<Class> classList, String... regular) {
        return doGetClasses(pkgPath, loader, FILTER_FOR_INTERFACE_OR_CLASS, classList, regular);
    }

    /**
     * 普通路径过滤
     *
     * @param pkgPath 类包路径 classes路径
     * @param regular 绝对路径过滤器 支持正则
     * @return 类集合
     */
    public static List<Class<?>> getClassesFilterForPath(String pkgPath, ClassLoader loader, String regular) {
        return doGetClasses(pkgPath, loader, FILTER_FOR_NOTHING, null, regular);
    }

    /**
     * 自定义过滤器
     *
     * @param pkgPath 类包路径 classes路径
     * @param regular 绝对路径过滤器 支持正则
     * @param filter  自定义过滤器 针对类的过滤规则
     * @return 类集合
     */
    public static List<Class<?>> getClassesFilterForCustom(String pkgPath, ClassLoader loader, String regular, BiPredicate<Class, Class> filter) {
        return doGetClasses(pkgPath, loader, filter, null, regular);
    }

    /**
     * 执行器
     * 采用类加载器，避免forName激活静态代码块 防止maven设置在其他服务依赖jar包时，把除了它本身编译后的jar 其他依赖全部去掉
     * 自身又未依赖时 导致类定义未找到 故全部catch住 不做异常处理
     */
    private static List<Class<?>> doGetClasses(String pkgPath, ClassLoader loader, BiPredicate<Class, Class> filter, List<Class> classList, String... regular) {
        List<Resource> resource = getResource(pkgPath, regular);
        List<Class<?>> classes = new ArrayList<>(resource.size());
        if (Objects.isNull(loader)) {
            loader = Thread.currentThread().getContextClassLoader();
        }

        for (Resource value : resource) {
            try {
                Class<?> aClass = loader.loadClass(REGISTER.getMetadataReader(value).getClassMetadata().getClassName());
                if (CollectionUtils.isNotEmpty(classList)) {
                    classList.stream().filter(s -> filter.test(s, aClass)).findFirst().ifPresent(s -> {
                        classes.add(aClass);
                    });
                } else {
                    if (filter.test(aClass, null)) {
                        classes.add(aClass);
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return classes;
    }

    public void log() {
        log.info("执行完成");
    }
}
