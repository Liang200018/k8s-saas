package com.lzy.k8s.saas.core.checker;

import com.lzy.k8s.saas.client.param.SaasAccountParam;
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

@Service
@Slf4j
public class SaasAccountChecker {

    @Resource
    private K8sSaasAccountMapper accountMapper;

    /**
     * hard check, if not login, throw
     *
     * @param account
     */
    public void checkLogin(SaasAccountParam account) {
        if (inLoginStatus(account.getUserName(), account.getPassword(), account.getPhone())) {
            return;
        }
        throw new SystemException(ErrorCode.BIZ_FAIL, "user not login, can not do the operation");
    }

    /**
     * soft check
     * @param userName
     * @param password
     * @param phone
     * @return
     */
    public boolean inLoginStatus(String userName, String password, String phone) {
        K8sSaasAccountInfo saasAccountInfo = new K8sSaasAccountInfo(userName, password, phone);

        K8sSaasAccountInfo searched = accountMapper.findByUserName(userName);

        // compare the userInfo
        if (searched != null && StringUtils.equals(searched.getUserName(), userName)
                && MessageDigestUtils.canMatch(password, searched.getPassword())
                && StringUtils.equals(searched.getPhone(), phone)
        ) {
            return AccountStatusEnum.LOGIN.getStatus().equals(searched.getStatus());
        }
        return false;
    }

    /**
     * when register, login, logout, check the param
     * @param saasAccountParam
     */
    public void checkAccountParam(SaasAccountParam saasAccountParam) {
        if (saasAccountParam.getUserName() == null || saasAccountParam.getPassword() == null || saasAccountParam.getPhone() == null) {
            throw new SystemException(ErrorCode.INVALID_PARAM, "[username, password, phone], required: yes");
        }
    }

}
