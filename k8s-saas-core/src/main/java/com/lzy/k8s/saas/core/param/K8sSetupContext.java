package com.lzy.k8s.saas.core.param;

import com.amazonaws.services.ec2.model.Instance;
import com.lzy.k8s.saas.client.param.EC2InstanceInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class K8sSetupContext {

    private String requestId;

    private String bizLogKey;

    private String username;
    private String password;

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

    private List<EC2InstanceInfo> instances;

    private Map<String, String> instanceId2JoinToken;

    private Map<String, Instance> instanceId2Info;

    /**
     * imply the host can be connected by password
     */
    private Map<String, Boolean> instanceId2PasswordAuthRst;

}
