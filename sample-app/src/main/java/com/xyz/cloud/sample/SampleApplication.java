package com.xyz.cloud.sample;

import com.xyz.cloud.CloudApplication;
import com.xyz.cloud.oauth1.annotation.EnableOAuth1;
import com.xyz.desensitize.annotation.EnableDesensitize;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@EnableDesensitize
@EnableOAuth1
@CloudApplication
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
