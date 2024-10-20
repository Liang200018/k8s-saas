package com.lzy.k8s.saas.client.dto;

import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class K8sClusterCreateDTO extends BaseDTO {

    private String pemFileUrl;

    private List<EC2InstanceInfo> successEc2Instances;

    /**
     * fail to create
     */
    private List<EC2InstanceInfo> failedEc2Instances;

    /**
     * imply the instances can be connected by password
     */
    private Map<String, Boolean> instanceId2PasswordAuthRst;
}
