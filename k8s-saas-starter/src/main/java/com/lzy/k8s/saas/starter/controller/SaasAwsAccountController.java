package com.lzy.k8s.saas.starter.controller;

import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import com.lzy.k8s.saas.client.param.AddAwsAccountRequest;
import com.lzy.k8s.saas.client.param.SaasAccountParam;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.client.result.Result;
import com.lzy.k8s.saas.core.checker.SaasAccountChecker;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.repo.mapper.AwsAccountMapper;
import com.lzy.k8s.saas.infra.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * manage the sass user's aws account
 */
@RestController
@RequestMapping("/aws")
@Slf4j
public class SaasAwsAccountController {

    @Resource
    private AwsAccountMapper awsAccountMapper;

    @Resource
    private SaasAccountChecker saasAccountChecker;

    /**
     * add aws account
     * @param addAwsAccountRequest
     * @return true: add success
     */
    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Boolean> addAwsAccount(@RequestBody AddAwsAccountRequest addAwsAccountRequest) {
        try {
            checkValid(addAwsAccountRequest);
            int inserted = awsAccountMapper.insertAccount(addAwsAccountRequest.getAwsAccountInfo());
            if (inserted > 0) {
                log.info("add aws account success");
                return Result.ofSuccess(true);
            }
            return Result.ofSuccess(false);
        } catch (Throwable throwable) {
            log.error("add aws account fail, param: {}, err: ", addAwsAccountRequest, throwable);
            return ResultUtils.getResult(throwable);
        }
    }

    private void checkValid(AddAwsAccountRequest request) {
        if (request == null || request.getSaasAccountParam() == null || request.getAwsAccountInfo() == null) {
            throw new SystemException(ErrorCode.INVALID_PARAM);
        }
        SaasAccountParam saasAccountParam = request.getSaasAccountParam();
        saasAccountChecker.checkLogin(saasAccountParam);
        if (!request.getAwsAccountInfo().valid()) {
            throw new SystemException(ErrorCode.INVALID_PARAM, "awsAccountInfo is not valid");
        }
    }
}
