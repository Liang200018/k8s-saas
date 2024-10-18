package com.lzy.k8s.saas.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;

/**
 * @Author: liangzhiyu
 * @Date: 2023/9/8
 */
@SpringBootApplication(
        exclude = {KafkaAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class},
        scanBasePackages = {"com.lzy.k8s.saas"})
@ImportResource({"classpath:*.xml"})
@ComponentScan(basePackages = {"com.lzy.k8s.saas"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
