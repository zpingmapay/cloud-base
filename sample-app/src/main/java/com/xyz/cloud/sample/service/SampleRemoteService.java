package com.xyz.cloud.sample.service;

import com.xyz.cloud.trace.annotation.Traceable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SampleRemoteService {
    @Traceable
    public String echo(String input) throws Exception {
        log.info("this is a remote echo method, {}", input);
        TimeUnit.SECONDS.sleep(2);

        return input;
    }
}
