package com.lzy.k8s.saas.infra.exception;

import com.lzy.k8s.saas.client.result.ErrorCode;

/**
 * @author yueqi
 * @date 2021/09/17
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 7517253339651549921L;

    /**
     * 错误码
     */
    private ErrorCode errorCode;

    /**
     * 附加信息
     */
    private String appendMsg;

    /**
     * 创建异常
     * @param errorCode
     * @param appendMsg
     * @param throwable
     */
    public BaseException(ErrorCode errorCode, String appendMsg, Throwable throwable) {
        super(appendMsg, throwable);
        this.errorCode = errorCode;
        this.appendMsg = appendMsg;
    }

    public BaseException(ErrorCode errorCode, String appendMsg) {
        this(errorCode, appendMsg, null);
    }

    public BaseException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getAppendMsg() {
        return appendMsg;
    }

    public void setAppendMsg(String appendMsg) {
        this.appendMsg = appendMsg;
    }
}
