package org.y.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.y.core.repository.jpa.ModuleJpa;
import org.y.core.repository.mapper.ModuleMapper;

@Component
public class ModuleRepository {
    public final ModuleMapper MAPPER;
    public final ModuleJpa JPA;

    @Autowired
    public ModuleRepository(ModuleMapper MAPPER, ModuleJpa JPA) {
        this.MAPPER = MAPPER;
        this.JPA = JPA;
    }
}
