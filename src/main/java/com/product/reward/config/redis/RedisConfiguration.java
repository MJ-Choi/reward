package com.product.reward.config.redis;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfiguration {

    private String server;
    private int port = 6379;
    private int database = 0;
    private long timeoutMs = 3000;

    private int maxConn = 50; //최대 커넥션 수
    private int maxWaitMs = 3000; // 대기시간 무제한
    private int minIdle = 50; // 최소 대기 커넥션 수
}
