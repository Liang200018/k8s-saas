package com.lzy.k8s.saas.gateway.http;

import com.lzy.k8s.saas.client.param.AwsAccountRequest;
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
@RequestMapping("/admin/aws")
@Slf4j
public class SaasAwsAccountController {

    @Resource
    private AwsAccountMapper awsAccountMapper;

    @Resource
    private SaasAccountChecker saasAccountChecker;

    /**
     * add aws user
     * @param awsAccountRequest
     * @return true: add success
     */
    @PostMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Boolean> addAwsAccount(@RequestBody AwsAccountRequest awsAccountRequest) {
        try {
            checkValid(awsAccountRequest);
            int inserted = awsAccountMapper.insertAccount(awsAccountRequest.getAwsAccountInfo());
            if (inserted > 0) {
                log.info("add aws account success");
                return Result.ofSuccess(true);
            }
            return Result.ofSuccess(false);
        } catch (Throwable throwable) {
            log.error("add aws account fail, param: {}, err: ", awsAccountRequest, throwable);
            return ResultUtils.getResult(throwable);
        }
    }

    private void checkValid(AwsAccountRequest request) {
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
