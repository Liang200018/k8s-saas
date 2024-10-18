package com.lzy.k8s.saas.client.result;

import com.lzy.k8s.saas.client.dto.BaseDTO;

/**
 * @Author: liangzhiyu
 * @Date: 2023/9/8
 */
public class Result<T> extends BaseDTO {

    private static final long serialVersionUID = 5241707271254629948L;
    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 数据
     */
    private T data;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 附加信息
     */
    private String appendMsg;

    private Result() {

    }

    /**
     * 返回成功
     * @param <T>
     * @return
     */
    public static <T> Result<T> ofSuccess() {
        return ofSuccess(null);
    }

    public static <T> Result<T> ofSuccess(T data, String appendMsg) {
        return of(null, data, appendMsg);
    }

    public static <T> Result<T> ofSuccess(T data) {
        return ofSuccess(data, null);
    }

    /**
     * 统一工厂方法
     * @param errorCode
     * @param data
     * @param appendMsg
     * @param <T>
     * @return
     */
    public static <T> Result<T> of(ErrorCode errorCode, T data, String appendMsg) {
        Result<T> result = new Result<>();
        result.appendMsg = appendMsg;
        if (errorCode != null) {
            result.errorCode = errorCode.getCode();
            result.errorMsg = errorCode.getErrorMsg();
            result.success = false;
        } else {
            result.success = true;
        }
        result.data = data;
        return result;
    }

    /**
     * 错误方法
     * @param <T>
     * @return
     */
    public static <T> Result<T> ofFail(ErrorCode errorCode) {
        return ofFail(errorCode, null);
    }

    public static <T> Result<T> ofFail() {
        return ofFail(ErrorCode.SYSTEM_FAIL, null);
    }

    public static <T> Result<T> ofFail(String appendMsg) {
        return of(ErrorCode.SYSTEM_FAIL, null, appendMsg);
    }

    public static <T> Result<T> ofFail(ErrorCode errorCode, String appendMsg) {
        return of(errorCode, null, appendMsg);
    }

    /**
     * 无效参数错误返回
     * @param <T>
     * @return
     */
    public static <T> Result<T> invalidParam() {
        return ofFail(ErrorCode.INVALID_PARAM);
    }

    public Boolean getSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getAppendMsg() {
        return appendMsg;
    }
}
