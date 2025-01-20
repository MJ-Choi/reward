package com.product.reward.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

    private boolean initDbDatas = false;
    private boolean initRedisDatas = false;
    private boolean useRedis = false;
    private int dbBulkSize = 10;

}