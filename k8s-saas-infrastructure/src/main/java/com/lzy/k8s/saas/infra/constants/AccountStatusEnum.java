package com.lzy.k8s.saas.infra.constants;

public enum AccountStatusEnum {

    REGISTER(0, "register"),
    LOGIN(1, "now login"),
    STOPPED(2, "the account has stopped");

    private Integer status;
    private String desc;

    AccountStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }
}
