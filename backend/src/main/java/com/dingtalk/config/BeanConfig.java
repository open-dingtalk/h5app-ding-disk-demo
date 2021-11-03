package com.dingtalk.config;

import com.aliyun.dingboot.common.login.DingTalkUser;
import com.aliyun.dingboot.common.token.ITokenManager;
import com.aliyun.dingboot.common.token.TokenManagerWithCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
