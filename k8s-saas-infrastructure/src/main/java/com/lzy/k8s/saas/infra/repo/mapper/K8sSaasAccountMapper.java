package com.lzy.k8s.saas.infra.repo.mapper;

import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface K8sSaasAccountMapper {

    int updateAccount(K8sSaasAccountInfo k8sSaasAccountInfo);


    int insertAccount(K8sSaasAccountInfo k8sSaasAccountInfo);


    K8sSaasAccountInfo findByUserName(@Param("user_name")  String userName);

}
