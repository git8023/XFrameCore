package org.y.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan({
        "org.y.core.config",
        "org.y.core.event",
        "org.y.core.repository",
        "org.y.core.service.impl",
        "org.y.core.web.controller",
        "org.y.core.web.converter",
        "org.y.core.web.error",
        "org.y.core.web.interceptor",
})
@MapperScan({"org.y.core.repository.mapper"})
@ServletComponentScan({"org.y.core.web.filter"})
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
