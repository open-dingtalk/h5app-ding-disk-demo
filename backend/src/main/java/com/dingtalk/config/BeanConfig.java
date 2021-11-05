package com.dingtalk.config;

import com.aliyun.dingboot.common.login.DingTalkUser;
import com.aliyun.dingboot.common.token.ITokenManager;
import com.aliyun.dingboot.common.token.TokenManagerWithCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class BeanConfig {

    @Bean
    public ITokenManager tokenManager() {
        return new TokenManagerWithCache();
    }

    @Bean
    public DingTalkUser dingTalkUser() {
        return new DingTalkUser();
    }

    @Bean
    public BlockingQueue<Long> blockingQueue() {
        return new LinkedBlockingQueue<>();
    }
}
