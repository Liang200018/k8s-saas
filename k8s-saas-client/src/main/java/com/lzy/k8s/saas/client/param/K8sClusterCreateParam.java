package com.lzy.k8s.saas.client.param;

import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import lombok.Data;

import java.util.List;

@Data
public class K8sClusterCreateParam extends BaseParam {

    private static final long serialVersionUID = 9127341400605279726L;

    private String requestId;

    // saas account
    private SaasAccountParam saasAccountParam;

    // common settings about the cluster
    private String keyPairName;
    private String vpcId;
    // method1 use a old sg
    private String securityGroupIds;
    // method2 new a sg
    private String groupName;
    private String groupDesc;

    private String subnetId;

    // spec
    private List<EC2InstanceInfo> instances;


}
