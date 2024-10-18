package com.lzy.k8s.saas.core.service;

import com.amazonaws.services.ec2.model.Instance;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.Session;
import com.lzy.k8s.saas.client.dto.K8sClusterCreateDTO;
import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import com.lzy.k8s.saas.client.param.K8sClusterCreateParam;
import com.lzy.k8s.saas.core.param.K8sSetupContext;
import com.lzy.k8s.saas.infra.param.Ec2ClientResult;
import com.lzy.k8s.saas.infra.remote.Ec2Remote;
import com.lzy.k8s.saas.infra.utils.JschUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class K8sClusterCreateService {

    @Resource
    private Ec2Remote ec2Remote;

    private K8sShellSetupService k8sShellSetupService;

    public K8sClusterCreateDTO createCluster(K8sClusterCreateParam param) {
        K8sSetupContext context = convert2Context(param);
        // prepare security group
        createSecurityGroup(context);
        createKeyPair(context);
        runInstances(context);
        // make sure connect to ec2 by password
        checkConnectByPwd(context);
        k8sShellSetupService.setup(context);
        return null;
    }


    private void checkConnectByPwd(K8sSetupContext context) {
        for (EC2InstanceInfo instanceInfo : context.getInstances()) {
            Session sshSession = JschUtils.getSshSession(context.getUsername(), context.getPassword(), instanceInfo.getPublicIpAddress(), 22, 600);
            if (sshSession.isConnected()) {
                context.getInstanceId2PasswordAuthRst().put(instanceInfo.getInstanceId(), Boolean.TRUE);
            }
            log.warn("{} can not auth by password", instanceInfo.getPublicIpAddress());
        }
    }

    private void runInstances(K8sSetupContext context) {
        for (EC2InstanceInfo spec : context.getInstances()) {
            Ec2ClientResult clientResult = ec2Remote.createInstance(spec);
            Instance instance = clientResult.getReservation().getInstances().get(0);
            // relate the ip
            spec.setInstanceId(instance.getInstanceId());
            spec.setPublicIpAddress(instance.getPublicIpAddress());
            spec.setPublicDnsName(instance.getPublicDnsName());
            // save instance
            context.getInstanceId2Info().put(instance.getInstanceId(), instance);
        }
    }

    private void createKeyPair(K8sSetupContext context) {
//        Ec2ClientResult clientResult = ec2Remote.createKeyPair(context.getKeyPairName());
        // todo save pem file
        context.setPemFileUrl(null);
    }

    private void createSecurityGroup(K8sSetupContext context) {
//        ec2Remote.createSecurityGroup(context.getGroupName(), context.getGroupDesc(), context.getVpcId());
    }

    private K8sSetupContext convert2Context(K8sClusterCreateParam param) {
        K8sSetupContext context = new K8sSetupContext();
        // todo fill the context

        context.setRequestId(param.getRequestId());
        // auth
        context.setKeyPairName(param.getKeyPairName());
        // network
        context.setVpcId(param.getVpcId());
        context.setSecurityGroupIds(param.getSecurityGroupIds());
        context.setGroupName(param.getGroupName());
        context.setGroupDesc(param.getGroupDesc());
        context.setSubnetId(param.getSubnetId());


        // fill the spec about common settings
        List<EC2InstanceInfo> specs = Lists.newArrayList();
        for (EC2InstanceInfo spec : specs) {
            spec.setSecurityGroupIds(param.getSecurityGroupIds());
            spec.setSubnetId(param.getSubnetId());
            spec.setKeyPairName(param.getKeyPairName());
            spec.setVpcId(param.getVpcId());
        }
        context.setInstances(specs);

        context.setInstanceId2Info(Maps.newHashMap());
        context.setInstanceId2JoinToken(Maps.newHashMap());
        context.setInstanceId2PasswordAuthRst(Maps.newHashMap());
        return context;
    }

}
