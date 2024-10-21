package com.lzy.k8s.saas.gateway.http;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.lzy.k8s.saas.client.param.SaasAccountParam;
import com.lzy.k8s.saas.core.checker.SaasAccountChecker;
import com.lzy.k8s.saas.core.service.K8sSaasAccountInfoService;
import com.lzy.k8s.saas.infra.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/demo/account")
@Slf4j
public class SaTokenDemoController {

    @Resource
    private K8sSaasAccountInfoService saasAccountInfoService;

    @Resource
    private SaasAccountChecker saasAccountChecker;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public SaResult login(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            // generate token and session
            StpUtil.login(saasAccountParam.getUserName());
            return SaResult.ok("log in success");
        } catch (Throwable e) {
            log.error("login api fail, param: {}, err: ", saasAccountParam, e);
            return SaResult.error(ResultUtils.getResult(e).getErrorMsg());
        }
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public SaResult logout(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            saasAccountChecker.checkAccountParam(saasAccountParam);
            StpUtil.logout();
            return SaResult.ok();
        } catch (Throwable e) {
            log.error("logout api fail, param: {}, err: ", saasAccountParam, e);
            return SaResult.error(ResultUtils.getResult(e).getErrorMsg());
        }
    }

    @GetMapping(value = "/authInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public SaResult getUserLogIn(@RequestBody SaasAccountParam saasAccountParam) {
        try {
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return SaResult.data(tokenInfo);
        } catch (Throwable e) {
            log.error("logout api fail, param: {}, err: ", saasAccountParam, e);
            return SaResult.error(ResultUtils.getResult(e).getErrorMsg());
        }
    }

}
