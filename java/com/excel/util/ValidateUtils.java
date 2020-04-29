package com.lyt.excel;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * @author liuyanting
 * @description 验证Utils
 * @date: 2018/12/17
 */
@Slf4j
public class ValidateUtils {

    private ValidateUtils() {
    }

    /**
     * @param object
     * @return boolean
     * @description 判断对象是否为空
     * @author liuyanting
     * @date 2018/12/17
     */
    public static boolean isObjectEmpty(Object object) {
        boolean isEmpty = false;
        if (null == object) {
            isEmpty = true;
        }
        if (object instanceof String && "".equals(object)) {
            isEmpty = true;
        }
        return isEmpty;
    }

    public static boolean isStringEmpty(String str) {
        return null == str || "".equals(str);
    }

    //form org.springframework.util.CollectionUtils
    public static boolean isCollectionEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    //form org.springframework.util.CollectionUtils
    public static boolean isMapEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    //from hualong
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public static String getDefaultIfNull(String str, String defaultValue) {
        return isStringEmpty(str) ? defaultValue : str;
    }

    public static boolean isYes(Integer code) {
        return Integer.valueOf(1).equals(code);
    }

    public static boolean isYes(int code) {
        return 1 == code;
    }
}
