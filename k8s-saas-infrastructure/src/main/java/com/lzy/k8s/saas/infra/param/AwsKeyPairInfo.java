package com.lzy.k8s.saas.infra.param;

import lombok.Data;

import java.util.Date;

@Data
public class AwsKeyPairInfo {

    private String accountId;


    private String keyName;

    private String keyMaterial;

    private Date createdTime;
}
