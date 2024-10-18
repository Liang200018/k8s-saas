package com.lzy.k8s.saas.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountsDTO extends BaseDTO {

    private String userName;
    private String phone;
    private Long userId;

    private Date registerTime;
    private Date latestLoginTime;
}
