package com.xyz.cloud.sample;

import com.xyz.cloud.CloudApplication;
import com.xyz.cloud.jwt.annotation.EnableJwt;
import com.xyz.cloud.lock.annotation.EnableLock;
import com.xyz.cloud.log.annotation.EnableControllerLog;
import com.xyz.cloud.retry.annotation.EnableRetryableEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@CloudApplication
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
