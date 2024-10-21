package com.lzy.k8s.saas.starter.controller;

import com.lzy.k8s.saas.client.dto.AccountsDTO;
import com.lzy.k8s.saas.client.param.SaasAccountParam;
import com.lzy.k8s.saas.client.result.Result;
import com.lzy.k8s.saas.core.checker.SaasAccountChecker;
import com.lzy.k8s.saas.core.service.K8sSaasAccountInfoService;
import com.lzy.k8s.saas.infra.constants.AccountStatusEnum;
import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import com.lzy.k8s.saas.infra.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * The endpoint is about manage the k8s saas account
 * Register, Login, Logout
 */
@RestController
@RequestMapping("account")
@Slf4j
public class LoginController {

    @Resource
    private K8sSaasAccountInfoService saasAccountInfoService;

    @Resource
    private SaasAccountChecker saasAccountChecker;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<AccountsDTO> register(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            K8sSaasAccountInfo accountInfo = saasAccountInfoService.register(saasAccountParam.getUserName(),
                    saasAccountParam.getPassword(), saasAccountParam.getPhone());
            return Result.ofSuccess(convert2DTO(accountInfo));
        } catch (Throwable e) {
            log.error("register api fail, param: {}, err: ", saasAccountParam, e);
            return ResultUtils.getResult(e);
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<AccountsDTO> login(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            K8sSaasAccountInfo accountInfo = saasAccountInfoService.login(saasAccountParam.getUserName(),
                    saasAccountParam.getPassword(), saasAccountParam.getPhone());
            return Result.ofSuccess(convert2DTO(accountInfo));
        } catch (Throwable e) {
            log.error("login api fail, param: {}, err: ", saasAccountParam, e);
            return ResultUtils.getResult(e);
        }
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<AccountsDTO> logout(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            K8sSaasAccountInfo cur = new K8sSaasAccountInfo(saasAccountParam.getUserName(), saasAccountParam.getPassword(), saasAccountParam.getPhone());
            K8sSaasAccountInfo accountInfo = saasAccountInfoService.logout(cur);
            return Result.ofSuccess(convert2DTO(accountInfo));
        } catch (Throwable e) {
            log.error("logout api fail, param: {}, err: ", saasAccountParam, e);
            return ResultUtils.getResult(e);
        }
    }

    @GetMapping(value = "/test")
    public Result<String> test(@RequestParam String userName, @RequestParam String password) {
        return Result.ofSuccess("login test");
    }

    private AccountsDTO convert2DTO(K8sSaasAccountInfo accountInfo) {
        // hiddenPassword
        AccountsDTO accountsDTO = new AccountsDTO();
        accountsDTO.setUserName(accountInfo.getUserName());
        accountsDTO.setPhone(accountInfo.getPhone());
        accountsDTO.setUserId(accountInfo.getUserId());
        accountsDTO.setRegisterTime(accountInfo.getRegisterTime());
        accountsDTO.setLatestLoginTime(accountInfo.getLatestLoginTime());

        accountsDTO.setStatusMsg(accountInfo.showAccountMsg());
        return accountsDTO;
    }
}
