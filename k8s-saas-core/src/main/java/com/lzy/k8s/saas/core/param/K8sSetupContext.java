package com.lzy.k8s.saas.core.param;

import com.amazonaws.services.ec2.model.Instance;
import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import com.lzy.k8s.saas.core.service.SaasEc2Client;
import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import com.lzy.k8s.saas.infra.param.AwsKeyPairInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class K8sSetupContext {

    private String requestId;

    private String bizLogKey;

    /**
     * saas account
     */
    private K8sSaasAccountInfo saasAccountInfo;

    /**
     * aws account
     */
    private AwsAccountInfo awsAccountInfo;

    // for log in the host by ssh
    private String linuxUsername;
    private String linuxPassword;

    // vpc manually set
    private String vpcId;
    private String subnetId;

    // either use id directly or new security group
    // method1
    private String securityGroupIds;
    // method2
    private String groupName;
    private String groupDesc;

    // auth
    private String pemFileUrl;
    private String keyPairName;
    // first launch, save keyMaterial(private key)
    private AwsKeyPairInfo keyPair;

    private List<EC2InstanceInfo> instances;

    private String workerNodeJoinToken;

    private Map<String, Instance> instanceId2Info;

    /**
     * imply the host can be connected by pem or password
     */
    private Map<String, Boolean> instanceId2AuthRst;

    private K8sSaasAccountInfo k8sSaasAccountInfo;

    // client
    private SaasEc2Client ec2Client;

    // master node
    private EC2InstanceInfo k8sMasterNode;

}
