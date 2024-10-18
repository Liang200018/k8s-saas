package com.lzy.k8s.saas.core.checker;

import com.lzy.k8s.saas.client.constant.K8sNodeRoleEnum;
import com.lzy.k8s.saas.client.param.K8sClusterCreateParam;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class CreateClusterChecker {


    public static boolean checkCluster(K8sClusterCreateParam param) {
        if (param == null || param.getVpcId() == null) {
            throw new SystemException(ErrorCode.INVALID_PARAM, "miss vpcId");
        }
        if (CollectionUtils.isEmpty(param.getInstances())) {
            throw new SystemException(ErrorCode.INVALID_PARAM, "ec2 instances is null");
        }
        // make sure at least one master node
        boolean masterPresent = param.getInstances().stream().anyMatch(spec -> StringUtils.equals(K8sNodeRoleEnum.MASTER_NODE.getCode(), spec.getInstanceType()));
        if (!masterPresent) {
            throw new SystemException(ErrorCode.INVALID_PARAM, "miss master node");
        }
        return true;
    }
}
