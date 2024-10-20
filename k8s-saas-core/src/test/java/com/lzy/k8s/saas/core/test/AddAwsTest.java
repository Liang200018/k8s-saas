package com.lzy.k8s.saas.core.test;

import com.amazonaws.regions.Regions;
import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import com.lzy.k8s.saas.core.service.K8sSaasAccountInfoService;
import com.lzy.k8s.saas.infra.repo.mapper.AwsAccountMapper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

public class AddAwsTest extends BaseCoreTest {

    @Resource
    private K8sSaasAccountInfoService accountInfoService;

    @Resource
    private AwsAccountMapper awsAccountMapper;


    @Test
    public void testAddAws() {
//        K8sSaasAccountInfo register = accountInfoService.register("biz_center", "biz_center", "17396124219");
//        K8sSaasAccountInfo login = accountInfoService.login("biz_center", "biz_center", "17396124219");
        AwsAccountInfo awsAccountInfo = new AwsAccountInfo();
        awsAccountInfo.setAccountId("867344467811");
        awsAccountInfo.setIamUser("can2");
        awsAccountInfo.setUserId(9L);
        awsAccountInfo.setRegion(Regions.AP_SOUTHEAST_2.getName());
        awsAccountInfo.setAccessKeyId("");
        awsAccountInfo.setSecretAccessKey("");
        awsAccountInfo.setCreatedTime(new Date());
        awsAccountInfo.setUpdatedTime(new Date());
        int inserted = awsAccountMapper.insertAccount(awsAccountInfo);
        assert inserted > 0;
    }
}
