package com.lzy.k8s.saas.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.Session;
import com.lzy.k8s.saas.client.constant.K8sNodeRoleEnum;
import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.core.param.K8sSetupContext;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.utils.JschUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class K8sShellSetupService {

    public void setup(K8sSetupContext setupContext) {
        setupK8sControlPlane(setupContext);
        setupWorkerNode(setupContext);

        generateJoinToken(setupContext);
        joinWorkerNode(setupContext);
    }

    private void setupK8sControlPlane(K8sSetupContext context) {
        Optional<EC2InstanceInfo> masterNode = context.getInstances().stream()
                .filter(a -> K8sNodeRoleEnum.MASTER_NODE.equals(a.getRole())).findFirst();
        if (!masterNode.isPresent()) {
            throw new SystemException(ErrorCode.INVALID_PARAM, "please fill the k8s cluster master node role");
        }
        context.setK8sMasterNode(masterNode.get());
        Session sshSession = JschUtils.getSshSession(context.getPemFileUrl(), context.getLinuxUsername(), context.getLinuxPassword(),
                masterNode.get().getPublicIpAddress(), 22, 600);
        try {
            JschUtils.execShellFromFile(sshSession, ResourceUtils.getFile("classpath:shell/test.sh"));
            JschUtils.execShellFromFile(sshSession, ResourceUtils.getFile("classpath:shell/install.sh"));
            List<String> cmds = JschUtils.extractCmdsFromFile(ResourceUtils.getFile("classpath:shell/master_node.sh"));

            // prepare variable
            Map<String, String> key2Value = Maps.newHashMap();
            key2Value.put("INSTANCE_PRIVATE_IP", masterNode.get().getPrivateIpAddress());

            // replace command variable
            List<String> newCmds = Lists.newArrayList();
            for (String cmd : cmds) {
                for (Map.Entry<String, String> entry : key2Value.entrySet()) {
                    String newCmd = cmd.replace(entry.getKey(), entry.getValue());
                    if (Objects.equals(newCmd, cmd)) {
                        continue;
                    } else {
                        log.info("old cmd: {}, new cmd: {}", cmd, newCmd);
                    }
                    newCmds.add(newCmd);
                }
            }
            cmds = newCmds;

            JschUtils.execCmdByShell(sshSession, cmds);
            JschUtils.closeAll(sshSession, "");
        } catch (Throwable e) {
            log.error("setupK8sControlPlane fail, err: ", e);
            throw new SystemException(ErrorCode.BIZ_FAIL, "setupK8sControlPlane fail");
        }

    }

    private void setupWorkerNode(K8sSetupContext context) {
        List<EC2InstanceInfo> workerNodes = context.getInstances().stream()
                .filter(a -> !K8sNodeRoleEnum.MASTER_NODE.getCode().equals(a.getRole())).collect(Collectors.toList());

        for (EC2InstanceInfo workerNode : workerNodes) {
            try {
                Session sshSession = JschUtils.getSshSession(context.getPemFileUrl(), context.getLinuxUsername(), context.getLinuxPassword(),
                        workerNode.getPublicIpAddress(), 22, 600);
                JschUtils.execShellFromFile(sshSession, ResourceUtils.getFile("classpath:shell/test.sh"));
                JschUtils.execShellFromFile(sshSession, ResourceUtils.getFile("classpath:shell/install.sh"));
                JschUtils.closeAll(sshSession, context.getPemFileUrl());
            } catch (Throwable e) {
                log.error("setupK8sControlPlane fail, err: ", e);
                throw new SystemException(ErrorCode.BIZ_FAIL, "setupWorkerNode fail");
            }
        }
    }

    private void generateJoinToken(K8sSetupContext context) {
        Session sshSession = JschUtils.getSshSession(context.getPemFileUrl(), context.getLinuxUsername(), context.getLinuxPassword(),
                context.getK8sMasterNode().getPublicIpAddress(), 22, 600);
        List<String> cmds = Lists.newArrayList();
        cmds.add("kubeadm token create --print-join-command");
        String token = JschUtils.execCmdByShell(sshSession, cmds);
        context.setWorkerNodeJoinToken(token);
    }

    private void joinWorkerNode(K8sSetupContext context) {
        if (StringUtils.isNotBlank(context.getWorkerNodeJoinToken())) {
            for (EC2InstanceInfo ec2InstanceInfo : context.getInstances()) {
                // skip master node
                if (Objects.equals(ec2InstanceInfo.getInstanceId(), context.getK8sMasterNode().getInstanceId())) {
                    continue;
                }
                // generate worker node session
                Session sshSession = JschUtils.getSshSession(context.getPemFileUrl(), context.getLinuxUsername(), context.getLinuxPassword(),
                        ec2InstanceInfo.getPublicIpAddress(), 22, 600);
                List<String> cmds = Lists.newArrayList();
                cmds.add(context.getWorkerNodeJoinToken());
                String rst = JschUtils.execCmdByShell(sshSession, cmds);
                log.info("instanceName: {}, join rst: {}", ec2InstanceInfo.getInstanceName(), rst);
            }
        }
    }
}
