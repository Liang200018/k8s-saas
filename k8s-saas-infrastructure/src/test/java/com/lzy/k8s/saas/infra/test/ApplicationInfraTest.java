package com.lzy.k8s.saas.infra.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class, KafkaAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class},
        scanBasePackages = {"com.lzy.init.project"})
@ImportResource({"classpath:*.xml"})
@ComponentScan(basePackages = {"com.lzy.init.project"})
public class ApplicationInfraTest {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationInfraTest.class, args);
    }
}
