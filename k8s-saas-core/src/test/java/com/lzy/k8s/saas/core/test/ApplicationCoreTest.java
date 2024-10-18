package com.lzy.k8s.saas.core.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * @Author: liangzhiyu
 * @Date: 2023/11/22
 */
@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class},
        scanBasePackages = {"com.lzy.k8s.saas"})
@ImportResource({"classpath:*.xml"})
@ComponentScan(basePackages = {"com.lzy.k8s.saas"})
public class ApplicationCoreTest {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationCoreTest.class, args);
    }
}

