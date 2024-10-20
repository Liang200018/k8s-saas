package com.lzy.k8s.saas.core.repo;

import com.lzy.k8s.saas.infra.param.AwsKeyPairInfo;

public interface KeyPairRepository {

    AwsKeyPairInfo find(String keyName);

    void remove(AwsKeyPairInfo awsKeyPairInfo);

    void save(AwsKeyPairInfo awsKeyPairInfo);

    /**
     * if keyName is same as others, save fail, return null
     * else success, return the pair created by aws
     * @param keyName
     * @return
     */
    AwsKeyPairInfo save(String keyName);
}
