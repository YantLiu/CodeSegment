package com.alt.base.common.dto.response;

import com.alibaba.fastjson.JSON;
import com.alt.base.common.enums.ErrorCodeEnum;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyanting
 * @description
 * @date: 2018/12/28
 */
@Getter
public class BaseRespDTO<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private boolean success;
    private T data;

    private void setSuccess() {
        this.success = ErrorCodeEnum.SUCCESS.getCode() == this.code;
    }

    public BaseRespDTO() {
        this.code = ErrorCodeEnum.SUCCESS.getCode();
        this.message = ErrorCodeEnum.SUCCESS.getMsg();
        this.success = true;
    }

    public BaseRespDTO(ErrorCodeEnum errorCodeEnum) {
        setCode(errorCodeEnum);
    }

    public BaseRespDTO(ErrorCodeEnum errorCodeEnum, String message) {
        setCode(errorCodeEnum);
        this.message = message;
    }

    public BaseRespDTO(ErrorCodeEnum errorCodeEnum, String message, T data) {
        setCode(errorCodeEnum);
        if (!StringUtils.isEmpty(message)) {
            this.message = message;
        }
        this.data = data;
    }

    public void setCode(ErrorCodeEnum errorCodeEnum) {
        this.code = errorCodeEnum.getCode();
        this.message = errorCodeEnum.getMsg();
        setSuccess();
    }

    public void setCode(int code) {
        this.code = code;
        setSuccess();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ArrayList<T> listToArrayList(List<T> list) {
        ArrayList<T> arrayList = new ArrayList();
        arrayList.addAll(list);
        return arrayList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
