package com.lzy.k8s.saas.client.param;

import lombok.Data;

import java.util.List;

@Data
public class K8sClusterCreateParam extends BaseParam {

    private String requestId;

    // common settings about the cluster
    private String keyPairName;
    private String vpcId;
    private String securityGroupIds;
    private String subnetId;

    // spec
    private List<EC2InstanceInfo> instances;


}
