package org.y.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.y.core.repository.jpa.UserJpa;
import org.y.core.repository.mapper.UserMapper;

@Component
public class UserRepository {

    public final UserMapper MAPPER;
    public final UserJpa JPA;

    @Autowired
    public UserRepository(UserMapper MAPPER, UserJpa JPA) {
        this.MAPPER = MAPPER;
        this.JPA = JPA;
    }
}
