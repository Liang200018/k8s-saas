package com.lzy.k8s.saas.infra.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountInfo {

    private  String accountId;

    private String iamUser;

    private String userId;

    private String status;

    private Date createdTime;

    private Date updatedTime;

}
