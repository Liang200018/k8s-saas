package com.lzy.k8s.saas.infra.param;

import com.lzy.k8s.saas.infra.constants.AccountStatusEnum;
import com.lzy.k8s.saas.infra.utils.MessageDigestUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
public class K8sSaasAccountInfo {

    // k8s saas platform
    // unique
    private String userName;
    private String password;
    private String phone;

    // auto generate
    private Long userId;

    /**
     * @see com.lzy.k8s.saas.infra.constants.AccountStatusEnum
     */
    private Integer status;

    private Date registerTime;

    private Date latestLoginTime;

    public K8sSaasAccountInfo(String userName, String password, String phone) {
        this.userName = userName;
        this.password = MessageDigestUtils.sha256Hex(password);
        this.phone = phone;
    }

    /**
     * decide the account is the same account
     * @param userName
     * @param password is text, not encrypted
     * @param phone
     * @return
     */
    public boolean sameAccount(String userName, String password, String phone) {
        return StringUtils.equals(this.userName, userName)
                && MessageDigestUtils.canMatch(password, this.password)
                && StringUtils.equals(this.phone, phone);
    }

    public boolean inLoginStatus() {
        return AccountStatusEnum.LOGIN.getStatus().equals(this.status);
    }

}
