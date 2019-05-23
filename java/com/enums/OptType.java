package com.alt.enterprise.enums;

/**
 * @author liuyanting
 * @description 添加/更新类型: 0 - 自己 1 - 供应商 2 - 客户 3 -分公司 4 - 保理商的客户
 * @date: 2018/12/20
 */
public enum OptType {
    SELF(0, "自己"),
    SUPPLIER(1, "供应商"),
    CUSTOMER(2, "客户"),
    SUBSIDIARY(3, "分公司");

    private final int code;
    private final String msg;

    OptType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isEqual(Integer code){
        return code  != null && code.equals(this.code);
    }
}
