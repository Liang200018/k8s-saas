package com.lzy.k8s.saas.core.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.google.common.collect.Lists;
import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import com.lzy.k8s.saas.client.param.ClientOption;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.param.Ec2ClientResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SaasEc2Client {

    Logger log = LoggerFactory.getLogger(SaasEc2Client.class.getName());

    private AmazonEC2 ec2;

    public SaasEc2Client(ClientOption clientOption) {
        this.ec2 = getClient(clientOption);
    }

    public AmazonEC2 getEc2() {
        return this.ec2;
    }

    /**
     * create group
     * @param groupName
     * @param groupDesc
     * @param vpcId
     * @return
     */
    public String createSecurityGroup(String groupName, String groupDesc, String vpcId) {
        try {
            CreateSecurityGroupRequest createRequest = new CreateSecurityGroupRequest()
                    .withGroupName(groupName)
                    .withDescription(groupDesc)
                    .withVpcId(vpcId);

            CreateSecurityGroupResult createResponse = ec2.createSecurityGroup(createRequest);

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
            return createResponse.getGroupId();
        } catch (Throwable e) {
            log.error("createSecurityGroup fail, groupName: {}, groupDesc: {}, vpcId: {}, e: ",
                    groupName, groupDesc, vpcId, e);
            throw new SystemException(ErrorCode.INVALID_PARAM);
        }

    }

    public Ec2ClientResult existKeyPair(String keyPairName) {
        // describe key pair
        DescribeKeyPairsResult describeKeyPairs = ec2.describeKeyPairs();
        boolean checkKey=false;
        for(KeyPairInfo keyPair : describeKeyPairs.getKeyPairs()) {
            if(keyPair.getKeyName().equalsIgnoreCase(keyPairName)) {
                checkKey=true;
                // convert KeyPairInfo to KeyPair
                KeyPair pair = new KeyPair().withKeyPairId(keyPair.getKeyPairId())
                        .withKeyName(keyPair.getKeyName())
                        .withKeyFingerprint(keyPair.getKeyFingerprint())
                        .withTags(keyPair.getTags());
                return Ec2ClientResult.builder().success(true)
                        .keyPair(pair).build();

            }
        }
        return Ec2ClientResult.builder().success(true).build();
    }

    public Ec2ClientResult getOrCreateKeyPair(String keyPairName) {
        try {
            Ec2ClientResult result = existKeyPair(keyPairName);
            if (result.getKeyPair() != null) {
                log.info("the key pair {} exist", keyPairName);
                return result;
            }
            // request
            CreateKeyPairRequest request = new CreateKeyPairRequest()
                    .withKeyName(keyPairName);
            // response
            CreateKeyPairResult response = ec2.createKeyPair(request);
            log.info(
                    "Successfully created key pair named {}",
                    keyPairName);
            return Ec2ClientResult.builder().success(true).keyPair(response.getKeyPair()).build();
        } catch (Throwable e) {
            log.error("create keyPairName {}, error: ", keyPairName, e);
            return Ec2ClientResult.builder().success(false).build();
        }
    }

    public Ec2ClientResult createInstance(EC2InstanceInfo spec) {
        try {
            RunInstancesRequest runRequest = new RunInstancesRequest()
                    .withImageId(spec.getImageId())
                    .withInstanceType(InstanceType.fromValue(spec.getInstanceType()))
                    .withKeyName(spec.getKeyPairName())
                    .withSecurityGroupIds(spec.getSecurityGroupIds())
                    .withSubnetId(spec.getSubnetId())
                    .withMaxCount(1)
                    .withMinCount(1);

            RunInstancesResult runResponse = ec2.runInstances(runRequest);
            String reservationId = runResponse.getReservation().getInstances().get(0).getInstanceId();
            Tag tag = new Tag()
                    .withKey("Name")
                    .withValue(spec.getInstanceName());
            CreateTagsRequest tagsRequest = new CreateTagsRequest()
                    .withResources(reservationId)
                    .withTags(tag);
            ec2.createTags(tagsRequest);
            log.info(
                    "Successfully started EC2 instance {} based on AMI {}",
                    reservationId, spec.getImageId());
            return Ec2ClientResult.builder().success(true).reservation(runResponse.getReservation()).build();
        } catch (Throwable e) {
            log.error("createInstance fail, info: {}, e: ", spec, e);
            throw new SystemException(ErrorCode.BIZ_FAIL);
        }
    }

    public Ec2ClientResult describeInstances() {
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        List<Instance> instanceList = Lists.newArrayList();
        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {
                instanceList.addAll(reservation.getInstances());
            }
            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }
        return Ec2ClientResult.builder().success(true).instances(instanceList).build();
    }

    /**
     * generate a client given region, Credentials
     * @param option
     * @return
     */
    public static AmazonEC2 getClient(ClientOption option) {
        AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {

                return new BasicAWSCredentials(option.getAccessKeyId(), option.getSecretAccessKey());
            }

            @Override
            public void refresh() {
                return;
            }
        };
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(option.getRegion())
                .withCredentials(awsCredentialsProvider).build();
        return ec2;
    }
}
