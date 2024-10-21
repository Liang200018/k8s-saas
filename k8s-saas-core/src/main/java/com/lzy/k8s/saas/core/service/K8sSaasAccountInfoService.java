package com.lzy.k8s.saas.core.service;

import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.constants.AccountStatusEnum;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import com.lzy.k8s.saas.infra.repo.mapper.K8sSaasAccountMapper;
import com.lzy.k8s.saas.infra.utils.MessageDigestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class K8sSaasAccountInfoService {

    @Resource
    private K8sSaasAccountMapper k8sSaasAccountMapper;

    public K8sSaasAccountInfo register(String userName, String password, String phone) {
        K8sSaasAccountInfo saasAccountInfo = new K8sSaasAccountInfo(userName, password, phone);
        saasAccountInfo.setStatus(AccountStatusEnum.REGISTER.getStatus());
        saasAccountInfo.setRegisterTime(new Date());

        K8sSaasAccountInfo accountInfo = k8sSaasAccountMapper.findByUserName(userName);

        String prompt = null;
        // make sure the username is unique
        if (accountInfo == null) {
            int insertCnt = k8sSaasAccountMapper.insertAccount(saasAccountInfo);
            if (insertCnt > 0) {
                return saasAccountInfo;
            }
            throw new SystemException(ErrorCode.ACCOUNT_ERROR, "generate account fail");
        }
        // the user has registered the account by the phone
        if (StringUtils.equals(phone, accountInfo.getPhone())) {
            prompt = "You have already registered!";
            log.error(prompt);
        } else {
            // others have the same username
            prompt = "the account username has already registered, please change a username";
        }
        log.error(prompt);
        throw new SystemException(ErrorCode.ACCOUNT_ERROR, prompt);
    }

    public K8sSaasAccountInfo login(String userName, String password, String phone) {
        K8sSaasAccountInfo saasAccountInfo = new K8sSaasAccountInfo(userName, password, phone);

        K8sSaasAccountInfo accountInfo = k8sSaasAccountMapper.findByUserName(userName);
        // compare the userInfo
        if (StringUtils.equals(accountInfo.getUserName(), userName)
                && MessageDigestUtils.canMatch(password, accountInfo.getPassword())
                && StringUtils.equals(accountInfo.getPhone(), phone)
        ) {
            accountInfo.setLatestLoginTime(new Date());
            accountInfo.setStatus(AccountStatusEnum.LOGIN.getStatus());
            int updated = k8sSaasAccountMapper.updateAccount(accountInfo);
            if (updated > 0) {
                log.info("username {} has successfully login", userName);
                return accountInfo;
            }

            // login fail
            log.error("update login status fail");
            saasAccountInfo.setLatestLoginTime(null);
            saasAccountInfo.setStatus(AccountStatusEnum.REGISTER.getStatus());
        }
        throw new SystemException(ErrorCode.ACCOUNT_ERROR, "login fail");
    }


    public K8sSaasAccountInfo logout(K8sSaasAccountInfo cur) {
        K8sSaasAccountInfo accountInfo = k8sSaasAccountMapper.findByUserName(cur.getUserName());
        if (accountInfo != null && StringUtils.equals(accountInfo.getUserName(), cur.getUserName())
                && StringUtils.equals(accountInfo.getPassword(), cur.getPassword())
                && StringUtils.equals(accountInfo.getPhone(), cur.getPhone())
        ) {
            // change login status to offline
            accountInfo.setStatus(AccountStatusEnum.REGISTER.getStatus());
            int updated = k8sSaasAccountMapper.updateAccount(accountInfo);
            if (updated > 0) {
                log.info("username {} has successfully logout", cur.getUserName());
                return accountInfo;
            }
            log.error("update logout status fail");
        }
        throw new SystemException(ErrorCode.ACCOUNT_ERROR, "logout fail");
    }

}
