package com.xyz.sample;

import com.xyz.client.annotation.EnableFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@EnableFeignClient
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SampleClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleClientApplication.class, args);
    }
}
