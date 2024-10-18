package com.lzy.k8s.saas.core.test;

import com.lzy.k8s.saas.core.service.K8sSaasAccountInfoService;
import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import com.lzy.k8s.saas.infra.repo.mapper.AwsAccountMapper;
import org.junit.Test;

import javax.annotation.Resource;

public class SaasAccountTest extends BaseCoreTest {

    @Resource
    private K8sSaasAccountInfoService accountInfoService;

    @Resource
    private AwsAccountMapper awsAccountMapper;

    @Test
    public void testInsertAccount() {
        K8sSaasAccountInfo register = accountInfoService.register("biz_center", "biz_center", "17396124219");
        System.out.println(register);
    }

    @Test
    public void testLoginAccount() {
        K8sSaasAccountInfo login = accountInfoService.login("biz_center", "biz_center", "17396124219");
        assert login.getUserId() != null;
    }
}
