package com.xyz.cloud.sample;

import com.xyz.client.annotation.EnableFeignClient;
import com.xyz.cloud.CloudApplication;
import com.xyz.cloud.oauth1.annotation.EnableOAuth1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClient
@EnableFeignClients(basePackages = "com.xyz.cloud.sample.client.feign")
@EnableOAuth1
@CloudApplication
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
