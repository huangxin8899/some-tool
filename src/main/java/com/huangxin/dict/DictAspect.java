package com.huangxin.dict;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.huangxin.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DictAspect
 *
 * @author 黄鑫
 */
@RestControllerAdvice
public class DictAspect implements ResponseBodyAdvice<Result<Object>> {

    @Resource
    private DictHandler dictHandler;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(Dict.class);
    }

    @Override
    public Result<Object> beforeBodyWrite(Result<Object> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (ObjectUtil.isNull(body) && ObjectUtil.isNull(body.getData())) {
            return body;
        }
        Object data = body.getData();
        Object target;
        if (data instanceof Collection) {
            target = ((Collection<?>) data).stream().map(this::translate).collect(Collectors.toList());
        } else {
            target = translate(data);
        }
        body.setData(target);
        return body;
    }

    private JSONObject translate(Object o) {
        Class<?> aClass = o.getClass();
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(o);
        Field[] dictFields = ReflectUtil.getFields(aClass, field -> field.isAnnotationPresent(Dict.class));
        for (Field dictField : dictFields) {
            Dict dict = dictField.getAnnotation(Dict.class);
            Map<String, String> map = dictHandler.handle(dict);
            String key = jsonObject.getString(dictField.getName());
            String value = map.getOrDefault(key, key);
            String targetName = StrUtil.isNotEmpty(dict.fieldName()) ? dict.fieldName() : dictField.getName() + dict.suffix();
            jsonObject.put(targetName, value);
        }
        return jsonObject;
    }


}
