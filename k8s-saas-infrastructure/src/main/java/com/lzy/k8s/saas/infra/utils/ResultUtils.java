package com.lzy.k8s.saas.infra.utils;

import com.lzy.k8s.saas.infra.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import com.lzy.k8s.saas.client.result.Result;

/**
 * @author yueqi
 * @date 2021/09/17
 */
@Slf4j
public class ResultUtils {

    /**
     * 基于异常和附加信息获取结果
     * @param throwable
     * @param <T>
     * @return
     */
    public static <T> Result<T> getResult(Throwable throwable, String appendMsg) {
        if (throwable == null) {
            return Result.ofFail(appendMsg);
        }

        if (throwable instanceof BaseException) {
            BaseException baseException = (BaseException) throwable;
            return Result.ofFail(baseException.getErrorCode(), baseException.getAppendMsg());
        } else {
            return Result.ofFail(appendMsg);
        }
    }

    /**
     * 基于异常获取结果
     * @param throwable
     * @param <T>
     * @return
     */
    public static <T> Result<T> getResult(Throwable throwable) {
        return getResult(throwable, null);
    }
}