package com.lzy.k8s.saas.infra.repo.mapper;

import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AwsAccountMapper {

    int insertAccount(AwsAccountInfo awsAccountInfo);


    AwsAccountInfo findByAccountId(@Param("account_id")  String accountId);
}
