package com.lzy.k8s.saas.gateway.http;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.lzy.k8s.saas.client.dto.AccountsDTO;
import com.lzy.k8s.saas.client.param.SaasAccountParam;
import com.lzy.k8s.saas.core.checker.SaasAccountChecker;
import com.lzy.k8s.saas.core.service.K8sSaasAccountInfoService;
import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import com.lzy.k8s.saas.infra.repo.mapper.K8sSaasAccountMapper;
import com.lzy.k8s.saas.infra.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private K8sSaasAccountMapper k8sSaasAccountMapper;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public SaResult register(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            K8sSaasAccountInfo accountInfo = saasAccountInfoService.register(saasAccountParam.getUserName(),
                    saasAccountParam.getPassword(), saasAccountParam.getPhone());
            return SaResult.data(convert2DTO(accountInfo));
        } catch (Throwable e) {
            log.error("register api fail, param: {}, err: ", saasAccountParam, e);
            return SaResult.error(ResultUtils.getResult(e).getErrorMsg());
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public SaResult login(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            K8sSaasAccountInfo accountInfo = saasAccountInfoService.login(saasAccountParam.getUserName(),
                    saasAccountParam.getPassword(), saasAccountParam.getPhone());
            // generate token and session
            StpUtil.login(accountInfo.getUserId(), TimeUnit.DAYS.toSeconds(1));
            return SaResult.data(accountInfo);
        } catch (Throwable e) {
            log.error("login api fail, param: {}, err: ", saasAccountParam, e);
            return SaResult.error(ResultUtils.getResult(e).getErrorMsg());
        }
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public SaResult logout(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            K8sSaasAccountInfo cur = new K8sSaasAccountInfo(saasAccountParam.getUserName(), saasAccountParam.getPassword(), saasAccountParam.getPhone());
            saasAccountInfoService.logout(cur);
            StpUtil.logout();
            return SaResult.ok("log out success");
        } catch (Throwable e) {
            log.error("logout api fail, param: {}, err: ", saasAccountParam, e);
            return SaResult.error(ResultUtils.getResult(e).getErrorMsg());
        }
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
