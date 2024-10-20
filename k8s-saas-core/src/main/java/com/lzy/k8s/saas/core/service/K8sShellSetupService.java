package com.lzy.k8s.saas.core.service;

import com.lzy.k8s.saas.core.param.K8sSetupContext;
import com.lzy.k8s.saas.infra.remote.Ec2Remote;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class K8sShellSetupService {

    @Resource
    private Ec2Remote ec2Remote;

    public void setup(K8sSetupContext setupContext) {
        // todo setup process
        setupK8sControlPlane(setupContext);
        setupContainRuntime(setupContext);

        generateJoinToken(setupContext);
        joinWorkerNode(setupContext);
    }

    private void setupK8sControlPlane(K8sSetupContext setupContext) {
    }

    private void setupContainRuntime(K8sSetupContext setupContext) {
    }

    private void generateJoinToken(K8sSetupContext setupContext) {
    }

    private void joinWorkerNode(K8sSetupContext setupContext) {
    }
}
