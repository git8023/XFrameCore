package org.y.core.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.y.core.config.Configure;
import org.y.core.service.ModuleService;

@Component
@Slf4j
public class OnDestroyEvent implements ApplicationListener<ContextClosedEvent> {

    private final ModuleService moduleService;
    private final Configure configure;

    @Autowired
    public OnDestroyEvent(ModuleService moduleService,
                          Configure configure) {
        this.moduleService = moduleService;
        this.configure = configure;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (configure.isShutdownModules()) {
            log.info("程序终止,关闭所有启动的模块...");
            moduleService.shutdownAll();
        }
    }
}