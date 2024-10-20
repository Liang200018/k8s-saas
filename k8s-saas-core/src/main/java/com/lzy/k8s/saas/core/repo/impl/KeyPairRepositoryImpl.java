package com.lzy.k8s.saas.core.repo.impl;

import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import com.lzy.k8s.saas.core.repo.KeyPairRepository;
import com.lzy.k8s.saas.core.service.SaasEc2Client;
import com.lzy.k8s.saas.infra.param.Ec2ClientResult;
import com.lzy.k8s.saas.infra.param.AwsKeyPairInfo;
import com.lzy.k8s.saas.infra.repo.mapper.AwsKeyPairMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Objects;

@Slf4j
public class KeyPairRepositoryImpl implements KeyPairRepository {


    private AwsAccountInfo awsAccountInfo;

    private final SaasEc2Client ec2Client;


    private AwsKeyPairMapper awsKeyPairMapper;

    public KeyPairRepositoryImpl(AwsAccountInfo awsAccountInfo, SaasEc2Client ec2Client) {
        this.awsAccountInfo = awsAccountInfo;
        this.ec2Client = ec2Client;
    }

    @Autowired
    public void setAwsKeyPairMapper(AwsKeyPairMapper awsKeyPairMapper) {
        this.awsKeyPairMapper = awsKeyPairMapper;
    }

    @Override
    public AwsKeyPairInfo find(String keyName) {
        AwsKeyPairInfo awsKeyPairInfo = awsKeyPairMapper.selectByKeyName(this.awsAccountInfo.getAccountId(), keyName);
        return awsKeyPairInfo;
    }

    @Override
    public void remove(AwsKeyPairInfo awsKeyPairInfo) {
        return;
    }

    @Override
    public void save(AwsKeyPairInfo awsKeyPairInfo) {

        int inserted = awsKeyPairMapper.insertKeyPair(awsKeyPairInfo);
        if (inserted > 0) {
            log.info("save aws key pair success");
        }
    }

    @Override
    public AwsKeyPairInfo save(String keyName) {
        // select
        AwsKeyPairInfo awsKeyPairInfo = find(keyName);
        if (awsKeyPairInfo != null) {
            log.error("save {} fail, database has the same key", keyName);
            return null;
        }
        // ec create
        Ec2ClientResult clientResult = ec2Client.createKeyPair(keyName);
        if (Objects.nonNull(clientResult.getKeyPair()) && Objects.nonNull(clientResult.getKeyPair().getKeyMaterial())) {
            AwsKeyPairInfo keyPairInfo = new AwsKeyPairInfo();
            keyPairInfo.setAccountId(awsAccountInfo.getAccountId());
            keyPairInfo.setKeyName(keyName);
            keyPairInfo.setKeyMaterial(clientResult.getKeyPair().getKeyMaterial());
            keyPairInfo.setCreatedTime(new Date());
            // save the key pair
            int inserted = awsKeyPairMapper.insertKeyPair(keyPairInfo);
            if (inserted > 0) {
                return keyPairInfo;
            }
            log.error("create aws key pair success, but fail to save it to database, aws key: {}", awsKeyPairInfo);
        }
        return null;
    }
}
