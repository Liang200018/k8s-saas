package com.lzy.k8s.saas.client.param;

import lombok.Data;
import lombok.ToString;

@Data
public class SaasAccountParam extends BaseParam {

    private static final long serialVersionUID = -1868245380299412071L;

    // register, login, logout
    private String bizType;

    // saas account
    private String userName;

    // not print password
    @ToString.Exclude
    private String password;
    private String phone;
}
