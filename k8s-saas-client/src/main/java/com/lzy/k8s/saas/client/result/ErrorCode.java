package com.lzy.k8s.saas.client.result;

/**
 * @Author: liangzhiyu
 * @Date: 2023/9/8
 */
public enum ErrorCode {

    /**
     * *****************通用错误码*************************
     */
    BIZ_FAIL("CB00001", "业务异常"),

    SYSTEM_FAIL("CB00002", "系统异常"),

    INVALID_PARAM("CB00003", "无效参数"),

    JSON_PARSE_FAIL("CB0004", "JSON解析失败"),

    SERVICE_NOT_AVAILABLE("CB0005", "服务开关降级未开启"),

    /**
     * *****************业务错误码************************
     */
    UNKNOWN_TYPE("CE00010", "未识别的类型"),

    QUERY_ERROR("CE100001", "参数构造出错"),

    ACCOUNT_ERROR("CE100002", "账户异常"),

    OPTIMISTIC_LOCKING_FAIL("CE100003", "乐观锁失败"),

    CONNECT_LINUX_FAIL("CE100004", "连接Linux失败");

    private String code;

    private String errorMsg;

    ErrorCode(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

