package com.lzy.k8s.saas.infra.repo.mapper;

import com.lzy.k8s.saas.infra.param.AwsKeyPairInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AwsKeyPairMapper {

    int insertKeyPair(AwsKeyPairInfo keyPairInfo);

    AwsKeyPairInfo selectByKeyName(@Param("account_id")  String accountId, @Param("key_name") String keyName);
}
