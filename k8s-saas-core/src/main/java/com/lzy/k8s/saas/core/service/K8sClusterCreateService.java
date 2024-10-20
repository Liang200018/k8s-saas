package com.lzy.k8s.saas.core.service;

import com.amazonaws.services.ec2.model.Instance;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.Session;
import com.lzy.k8s.saas.client.dto.K8sClusterCreateDTO;
import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import com.lzy.k8s.saas.client.param.ClientOption;
import com.lzy.k8s.saas.client.param.K8sClusterCreateParam;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.core.checker.SaasAccountChecker;
import com.lzy.k8s.saas.core.param.K8sSetupContext;
import com.lzy.k8s.saas.core.repo.KeyPairRepository;
import com.lzy.k8s.saas.core.repo.impl.KeyPairRepositoryImpl;
import com.lzy.k8s.saas.core.utils.PemInMemoryUtils;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.param.Ec2ClientResult;
import com.lzy.k8s.saas.infra.param.K8sSaasAccountInfo;
import com.lzy.k8s.saas.infra.param.AwsKeyPairInfo;
import com.lzy.k8s.saas.infra.repo.mapper.AwsAccountMapper;
import com.lzy.k8s.saas.infra.repo.mapper.AwsKeyPairMapper;
import com.lzy.k8s.saas.infra.utils.JschUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class K8sClusterCreateService {

    @Resource
    private AwsAccountMapper awsAccountMapper;

    @Resource
    private AwsKeyPairMapper awsKeyPairMapper;

    @Resource
    private SaasAccountChecker saasAccountChecker;

    @Resource
    private K8sShellSetupService k8sShellSetupService;

    public K8sClusterCreateDTO createCluster(K8sClusterCreateParam param) {
        K8sSetupContext context = convert2Context(param);
        // prepare security group
        prepareProcess(context);
        runInstancesProcess(context);
        // make sure connect to ec2 by password
        boolean ready = preCheckSetupProcess(context);
        if (!ready) {
            return convert2DTO(context);
        }
        k8sShellSetupService.setup(context);
        return convert2DTO(context);
    }

    /**
     * check all conditions, in order to set up the k8s-cluster's nodes at the same time.
     * @param context
     * @return
     */
    private boolean preCheckSetupProcess(K8sSetupContext context) {
        checkConnect(context);
        return context.getInstanceId2AuthRst().values().stream().allMatch(BooleanUtils::isTrue);
    }


    /**
     * connect the instances
     * @param context
     */
    private void checkConnect(K8sSetupContext context) {
        for (EC2InstanceInfo instanceInfo : context.getInstances()) {
            try {
                Session sshSession = JschUtils.getSshSession(context.getPemFileUrl(), context.getLinuxUsername(), context.getLinuxPassword(),
                        instanceInfo.getPublicIpAddress(), 22, 600);
                context.getInstanceId2AuthRst().put(instanceInfo.getInstanceId(), Boolean.TRUE);
                JschUtils.closeAll(sshSession, context.getPemFileUrl());
            } catch (Throwable e) {
                log.warn("{} can not auth by password", instanceInfo.getPublicIpAddress());
                context.getInstanceId2AuthRst().put(instanceInfo.getInstanceId(), Boolean.FALSE);
            }
        }
    }

    /**
     * for loop to launch a new instance
     * if the instance has been created then do nothing, or else create
     * @param context
     */
    private void runInstancesProcess(K8sSetupContext context) {
        SaasEc2Client ec2Client = context.getEc2Client();
        Ec2ClientResult result = ec2Client.describeInstances();

        Map<String, Instance> createdId2Instance = result.getInstances().stream()
                .collect(Collectors.toMap(Instance::getInstanceId, a -> a, (a, b) -> a));
        for (EC2InstanceInfo spec : context.getInstances()) {
            Instance createdbySpec = null;
            if (createdId2Instance.containsKey(spec.getInstanceId())) {
                createdbySpec = createdId2Instance.get(spec.getInstanceId());
            } else {
                // if not exist
                try {
                    Ec2ClientResult clientResult = ec2Client.createInstance(spec);
                    createdbySpec = clientResult.getReservation().getInstances().get(0);
                } catch (Throwable e) {
                    log.error("create instance fail, param: {}, err: ", spec, e);
                }
            }
            if (Objects.nonNull(createdbySpec)) {
                // relate the ip
                spec.setInstanceId(createdbySpec.getInstanceId());
                spec.setPublicIpAddress(createdbySpec.getPublicIpAddress());
                spec.setPrivateIpAddress(createdbySpec.getPrivateIpAddress());
                spec.setPublicDnsName(createdbySpec.getPublicDnsName());
                // save instance
                context.getInstanceId2Info().put(createdbySpec.getInstanceId(), createdbySpec);
            }
        }
    }


    private void prepareProcess(K8sSetupContext context) {
        SaasEc2Client ec2Client = context.getEc2Client();
        // prepare security group
        if (context.getSecurityGroupIds() == null && Objects.nonNull(context.getGroupName())
                && Objects.nonNull(context.getVpcId())) {
            String securityGroup = ec2Client.createSecurityGroup(context.getGroupName(), context.getGroupDesc(), context.getVpcId());
            context.setSecurityGroupIds(securityGroup);
        }
        // prepare key pair
        KeyPairRepository keyPairRepo = new KeyPairRepositoryImpl(context.getAwsAccountInfo(), ec2Client, awsKeyPairMapper);
        AwsKeyPairInfo awsKeyPairInfo = keyPairRepo.find(context.getKeyPairName());
        if (Objects.nonNull(awsKeyPairInfo)) {
            context.setKeyPair(awsKeyPairInfo);
        } else {
            AwsKeyPairInfo save = keyPairRepo.save(context.getKeyPairName());
            if (Objects.nonNull(save)) {
                context.setKeyPair(save);
            } else {
                throw new SystemException(ErrorCode.INVALID_PARAM, "fail to write the key to database");
            }
        }
        // write key.pem file
        if (Objects.nonNull(context.getKeyPair().getKeyMaterial())) {
            Path savePemFile = PemInMemoryUtils.savePemFile(context.getAwsAccountInfo().getAccountId(),
                    context.getKeyPairName(),
                    context.getKeyPair().getKeyMaterial());
            if (Objects.nonNull(savePemFile)) {
                context.setPemFileUrl(savePemFile.toString());
            }
        }
        if (context.getPemFileUrl() == null) {
            throw new SystemException(ErrorCode.ACCOUNT_ERROR, "without key pair");
        }
    }

    private K8sSetupContext convert2Context(K8sClusterCreateParam param) {
        K8sSetupContext context = new K8sSetupContext();
        // must log in or throw Exception
        K8sSaasAccountInfo saasAccountInfo = saasAccountChecker.checkLogin(param.getSaasAccountParam());
        context.setSaasAccountInfo(saasAccountInfo);

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
        List<EC2InstanceInfo> specs = param.getInstances();
        for (EC2InstanceInfo spec : specs) {
            spec.setSecurityGroupIds(param.getSecurityGroupIds());
            spec.setSubnetId(param.getSubnetId());
            spec.setKeyPairName(param.getKeyPairName());
            spec.setVpcId(param.getVpcId());
        }
        context.setInstances(specs);

        context.setInstanceId2Info(Maps.newHashMap());
        // context.setWorkerNodeJoinToken(null);
        context.setInstanceId2AuthRst(Maps.newHashMap());

        // find aws account and produce aws client.
        AwsAccountInfo awsAccountInfo = awsAccountMapper.findByUserId(context.getSaasAccountInfo().getUserId());
        if (awsAccountInfo == null) {
            throw new SystemException(ErrorCode.BIZ_FAIL, "aws user account is null");
        }
        context.setAwsAccountInfo(awsAccountInfo);
        context.setEc2Client(new SaasEc2Client(ClientOption.builder().region(awsAccountInfo.getRegion())
                .accessKeyId(awsAccountInfo.getAccessKeyId())
                .secretAccessKey(awsAccountInfo.getSecretAccessKey()).build())
        );
        return context;
    }

    private K8sClusterCreateDTO convert2DTO(K8sSetupContext context) {
        K8sClusterCreateDTO clusterCreateDTO = new K8sClusterCreateDTO();
        // todo update the instances

        clusterCreateDTO.setSuccessEc2Instances(null);
        clusterCreateDTO.setFailedEc2Instances(null);
        clusterCreateDTO.setInstanceId2PasswordAuthRst(context.getInstanceId2AuthRst());
        return clusterCreateDTO;
    }

}
