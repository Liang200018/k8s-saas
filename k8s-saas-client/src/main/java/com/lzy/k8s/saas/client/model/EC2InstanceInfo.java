package com.lzy.k8s.saas.client.model;

import com.lzy.k8s.saas.client.constant.K8sNodeRoleEnum;
import lombok.Data;

/**
 * maintain the attribute of spec and real ec2 instance
 */

@Data
public class EC2InstanceInfo {

    /**
     * required: yes
     */
    private K8sNodeRoleEnum role;

    /**
     * required: yes
     */
    private String imageId;


    private String  instanceName;

    /**
     * required: yes
     * @See com.amazonaws.services.ec2.model.InstanceType
     */
    private String instanceType;


    private String vpcId;
    private String subnetId;

    private String securityGroupIds;

    // auth
    private String keyPairName;

    private Integer cnt;


    // after the instance launch, update the attribute
    private String instanceId;
    private String publicDnsName;
    private String publicIpAddress;

}
