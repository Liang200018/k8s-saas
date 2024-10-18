package com.lzy.k8s.saas.client.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.Objects;

@Data
public class AwsAccountInfo {

    // saas user
    private Long userId;

    // aws global account
    private  String accountId;
    private String iamUser;
    // for Ec2Client connect
    private String region;

    @ToString.Exclude
    private String accessKeyId;
    @ToString.Exclude
    private String secretAccessKey;

    private Date createdTime;

    private Date updatedTime;

    public boolean valid() {
        return Objects.nonNull(this.accountId) && Objects.nonNull(iamUser)
                && Objects.nonNull(this.accessKeyId) && Objects.nonNull(this.secretAccessKey);
    }
}
