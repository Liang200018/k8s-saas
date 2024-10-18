package com.lzy.k8s.saas.infra.remote;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.lzy.k8s.saas.client.param.EC2InstanceInfo;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.param.Ec2ClientResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

@Service
@Slf4j
public class Ec2Remote {

    @Resource
    private PemManager pemManager;

    public void createSecurityGroup(String groupName, String groupDesc, String vpcId) {
        try {
            final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

            CreateSecurityGroupRequest createRequest = new CreateSecurityGroupRequest()
                    .withGroupName(groupName)
                    .withDescription(groupDesc)
                    .withVpcId(vpcId);

            CreateSecurityGroupResult create_response = ec2.createSecurityGroup(createRequest);

            log.info(
                    "Successfully created security group named {}",
                    groupName);

            IpRange ipRange = new IpRange()
                    .withCidrIp("0.0.0.0/0");

            IpPermission ipPerm = new IpPermission()
                    .withIpProtocol("all")
                    .withIpv4Ranges(ipRange);

            AuthorizeSecurityGroupIngressRequest authRequest = new AuthorizeSecurityGroupIngressRequest()
                    .withGroupName(groupName)
                    .withIpPermissions(ipPerm);

            AuthorizeSecurityGroupIngressResult authResponse = ec2.authorizeSecurityGroupIngress(authRequest);

            log.info(
                    "Successfully added ingress policy to security group {}",
                    groupName);
        } catch (Throwable e) {
            log.error("createSecurityGroup fail, groupName: {}, groupDesc: {}, vpcId: {}, e: ",
                    groupName, groupDesc, vpcId, e);
            throw new SystemException(ErrorCode.INVALID_PARAM);
        }

    }

    public boolean existKeyPair(String keyPairName) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        // describe key pair
        DescribeKeyPairsResult describeKeyPairs = ec2.describeKeyPairs();
        boolean checkKey=false;
        for(KeyPairInfo key_pair : describeKeyPairs.getKeyPairs()) {
            if(key_pair.getKeyName().equalsIgnoreCase(keyPairName)) {
                checkKey=true;
                break;
            }
        }
        return checkKey;
    }

    public Ec2ClientResult createKeyPair(String keyPairName) {
        Ec2ClientResult clientResult = new Ec2ClientResult();
        try {
            final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
            boolean checkKey = existKeyPair(keyPairName);
            if(!checkKey) {
                // request
                CreateKeyPairRequest request = new CreateKeyPairRequest()
                        .withKeyName(keyPairName);
                // response
                CreateKeyPairResult response = ec2.createKeyPair(request);
                log.info(
                        "Successfully created key pair named {}",
                        keyPairName);
            } else {
                log.info("the key pair {} exist", keyPairName);
            }
        } catch (Throwable e) {
            log.error("create keyPairName {}, error: ", keyPairName, e);
        }
        return clientResult;
    }

    public Ec2ClientResult createInstance(EC2InstanceInfo spec) {
        Ec2ClientResult result = new Ec2ClientResult();
        try {
            final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

            RunInstancesRequest run_request = new RunInstancesRequest()
                    .withImageId(spec.getImageId())
                    .withInstanceType(InstanceType.fromValue(spec.getInstanceType()))
                    .withKeyName(spec.getKeyPairName())
                    .withSecurityGroupIds(spec.getSecurityGroupIds())
                    .withSubnetId(spec.getSubnetId())
                    .withMaxCount(1)
                    .withMinCount(1);

            RunInstancesResult run_response = ec2.runInstances(run_request);

            String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

            Tag tag = new Tag()
                    .withKey("Name")
                    .withValue(spec.getInstanceName());

            CreateTagsRequest tag_request = new CreateTagsRequest()
                    .withResources(reservation_id)
                    .withTags(tag);

            CreateTagsResult tag_response = ec2.createTags(tag_request);

            log.info(
                    "Successfully started EC2 instance {} based on AMI {}",
                    reservation_id, spec.getImageId());
            result.setReservation(run_response.getReservation());
            return result;
        } catch (Throwable e) {
            log.error("createInstance fail, info: {}, e: ", spec, e);
            throw new SystemException(ErrorCode.BIZ_FAIL);
        }
    }
}
