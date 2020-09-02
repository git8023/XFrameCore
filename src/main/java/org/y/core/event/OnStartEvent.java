package org.y.core.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.y.core.config.Configure;
import org.y.core.service.ModuleService;
import org.y.core.util.Constants;
import org.y.core.util.ModuleUtil;

@Component
@Slf4j
public class OnStartEvent implements ApplicationRunner {

    private final ModuleService moduleService;
    private final Configure configure;

    @Autowired
    public OnStartEvent(ModuleService moduleService,
                        Configure configure) {
        this.moduleService = moduleService;
        this.configure = configure;
        ModuleUtil.BAT_DIRECTORY= configure.getBatDirectory();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (configure.isShutdownModules()) {
            log.info("关闭所有已启动模块...");
            moduleService.shutdownAll();
        }

        log.info("启动系统服务...");
        moduleService.startSystemServices();
    }

}
