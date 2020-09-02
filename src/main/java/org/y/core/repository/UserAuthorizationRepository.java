package org.y.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.y.core.repository.jpa.UserAuthorizationJpa;

@Component
public class UserAuthorizationRepository {

    public final UserAuthorizationJpa JPA;

    @Autowired
    public UserAuthorizationRepository(UserAuthorizationJpa JPA) {
        this.JPA = JPA;
    }
}
