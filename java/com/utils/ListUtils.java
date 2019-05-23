package com.alt.base.common.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyanting
 * @description list工具类
 * @date: 2019/04/26
 */
public class ListUtils {
    private ListUtils(){
    }

    public static <T> ArrayList<T> listToArrayList(List<T> list) {
        ArrayList<T> arrayList = new ArrayList<>();
        arrayList.addAll(list);
        return arrayList;
    }

    public static <targetClz, sourceClz> List<targetClz> copyList(List<sourceClz> sourceList, Class targetClz) throws IllegalAccessException, InstantiationException {
        List<targetClz> arrayList = new ArrayList<targetClz>();
        for (sourceClz source : sourceList) {
            targetClz target = (targetClz) targetClz.newInstance();
            arrayList.add(target);
            BeanUtils.copyProperties(source, target);
        }
        return arrayList;
    }
}
