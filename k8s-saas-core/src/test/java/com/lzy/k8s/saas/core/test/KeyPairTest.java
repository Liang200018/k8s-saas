package com.lzy.k8s.saas.core.test;

import com.amazonaws.regions.Regions;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import com.lzy.k8s.saas.client.param.ClientOption;
import com.lzy.k8s.saas.core.service.SaasEc2Client;
import com.lzy.k8s.saas.core.utils.PemInMemoryUtils;
import com.lzy.k8s.saas.infra.param.Ec2ClientResult;
import com.lzy.k8s.saas.infra.repo.mapper.AwsAccountMapper;
import com.lzy.k8s.saas.infra.utils.FileRepository;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KeyPairTest extends BaseCoreTest {

    @Resource
    private AwsAccountMapper awsAccountMapper;

    @Resource
    private FileRepository fileRepository;

    @Test
    public void testGetKeyPair() {
        AwsAccountInfo awsAccountInfo = awsAccountMapper.findByUserId(9L);
        awsAccountInfo.setRegion(Regions.AP_SOUTHEAST_2.getName());
        SaasEc2Client ec2Client = new SaasEc2Client(ClientOption.builder().region(awsAccountInfo.getRegion())
                .accessKeyId(awsAccountInfo.getAccessKeyId())
                .secretAccessKey(awsAccountInfo.getSecretAccessKey()).build());
        Ec2ClientResult result = ec2Client.createKeyPair("test-create-pair");
        assert result.getKeyPair() != null;
    }

    @Test
    public void test() throws IOException {
        Path test = PemInMemoryUtils.savePemFile("123", "test", "123456");
        System.out.println(test.toAbsolutePath());
        System.out.println(PemInMemoryUtils.readPemFile(test));

        PemInMemoryUtils.deletePemFile(test);
        System.out.println(PemInMemoryUtils.readPemFile(test));
    }
}
