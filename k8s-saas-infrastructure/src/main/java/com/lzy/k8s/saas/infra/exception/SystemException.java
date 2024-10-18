package com.lzy.k8s.saas.infra.exception;

import com.lzy.k8s.saas.client.result.ErrorCode;

/**
 * @author hanxin.feng
 */
public class SystemException extends BaseException {
    private static final long serialVersionUID = -4686826828314844684L;

    public SystemException(ErrorCode errorCode, String appendMsg, Throwable throwable) {
        super(errorCode, appendMsg, throwable);
    }

    public SystemException(ErrorCode errorCode, String appendMsg) {
        super(errorCode, appendMsg);
    }

    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }
}
