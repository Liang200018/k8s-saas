package com.lzy.k8s.saas.client.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum K8sNodeRoleEnum {
    MASTER_NODE("master-node", "master-node"),
    WORKER_NODE("worker-node", "worker-node");

    private String code;
    private String desc;

    K8sNodeRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<String, K8sNodeRoleEnum> map = new HashMap<>();

    static {
        for (K8sNodeRoleEnum roleEnum : values()) {
            map.put(roleEnum.code, roleEnum);
        }
    }

    public static K8sNodeRoleEnum parseRoleByCode(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            return null;
        }
        return map.get(roleCode);
    }

    public String getCode() {
        return code;
    }
}
