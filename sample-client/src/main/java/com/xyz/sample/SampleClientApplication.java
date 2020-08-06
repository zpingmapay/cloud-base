package com.xyz.sample;

import com.xyz.client.annotation.EnableFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClient
@EnableFeignClients(basePackages = "com.xyz.sample.client")
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SampleClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleClientApplication.class, args);
    }
}
