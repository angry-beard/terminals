package com.beard.terminals.pipeline.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Slf4j
public class JsonUtils {
    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     */
    public static String toJSONString(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("对象转换异常", e);
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     */
    public static <T> T parseObj(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            log.warn("对象转换异常", e);
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     */
    public static <T> T parseArr(String jsonData, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(jsonData, typeReference);
        } catch (Exception e) {
            log.warn("对象转换异常", e);
        }
        return null;
    }
}