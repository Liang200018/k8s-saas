package com.lzy.k8s.saas.core.checker;

import com.lzy.k8s.saas.client.param.SaasAccountParam;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import com.lzy.k8s.saas.infra.repo.mapper.K8sSaasAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SaasAccountChecker {

    @Resource
    private K8sSaasAccountMapper accountMapper;

    /**
     * hard check, if not login, throw
     * @param accountParam
     * @return
     */
    public K8sSaasAccountInfo checkLogin(SaasAccountParam accountParam) {
        K8sSaasAccountInfo loginAccount = getLoginAccount(accountParam.getUserName(), accountParam.getPassword(), accountParam.getPhone());
        if (loginAccount != null) {
            return loginAccount;
        }
        throw new SystemException(ErrorCode.BIZ_FAIL, "user not login, can not do the operation");
    }

    /**
     * soft check, null means the account is not registered or not login
     * @param userName
     * @param password
     * @param phone
     * @return
     */
    public K8sSaasAccountInfo getLoginAccount(String userName, String password, String phone) {
        K8sSaasAccountInfo searched = accountMapper.findByUserName(userName);
        // compare the userInfo
        if (searched != null && searched.sameAccount(userName, password, phone) && searched.inLoginStatus()) {
            return searched;
        }
        return null;
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
