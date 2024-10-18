package com.lzy.k8s.saas.infra.repo.mapper;

import com.lzy.k8s.saas.infra.param.AccountInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AccountMapper {

    int updateAccount(AccountInfo accountInfo);

    int createAccount(AccountInfo accountInfo);

    List<AccountInfo> findByUserId(@Param("user_id")  String userId);

    AccountInfo findByAccountId(@Param("account_id") String accountId);
}
