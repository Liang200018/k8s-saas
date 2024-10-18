package com.lzy.k8s.saas.core.test;

import com.lzy.k8s.saas.infra.remote.Ec2Remote;
import org.junit.Test;

import javax.annotation.Resource;

public class KeyPairTest extends BaseCoreTest {
    @Resource
    private Ec2Remote ec2Remote;

    @Test
    public void testKeyPair() {
//        boolean testAwiCli2 = ec2Remote.existKeyPair("TestAwiCli2");
//        System.out.println(testAwiCli2);
    }
}
