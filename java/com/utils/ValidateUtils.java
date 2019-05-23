package com.alt.base.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author liuyanting
 * @description 验证Utils
 * @date: 2018/12/17
 */
@Slf4j
public class ValidateUtils {

    private ValidateUtils() {}
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

    //from hualong
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}
