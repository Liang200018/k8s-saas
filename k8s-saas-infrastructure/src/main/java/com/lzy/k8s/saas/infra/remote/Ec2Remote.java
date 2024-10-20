package com.lzy.k8s.saas.infra.remote;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.lzy.k8s.saas.client.model.EC2InstanceInfo;
import com.lzy.k8s.saas.client.param.ClientOption;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import com.lzy.k8s.saas.infra.param.Ec2ClientResult;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Ec2Remote {




}
