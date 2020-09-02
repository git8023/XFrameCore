package org.y.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "x-core-config")
@Data
public class Configure {

    /**
     * Xcore停止时关闭所有开启的Module
     */
    private boolean shutdownModules;

    /**
     * 模块文件上传目录
     */
    private String moduleDirectory;

    /**
     * bat执行目录
     */
    private String batDirectory;

}
