package com.alt.enterprise.enums;

/**
 * @author liuyanting
 * @description 文件服务器-企业系统
 * @date: 2018/12/20
 */
public enum AttachType {
    BUSINESS_LICENSE("business_license", "营业执照"),
    QUALIFICATION_CERTIFICATE("qualification_certificate", "资质证书"),
    OTHER_ATTACH("other_attach", "企业其他附件");

    private final String code;
    private final String msg;
    public static final String BUSINESS_TYPE = "scf-enterprise";


    AttachType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isEqual(String code){
        return this.code.equals(code);
    }
}
