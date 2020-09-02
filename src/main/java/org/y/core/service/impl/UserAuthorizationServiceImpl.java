package org.y.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.y.core.model.entity.UserAuthorize;
import org.y.core.repository.UserAuthorizationRepository;
import org.y.core.service.UserAuthorizationService;

@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService {

    private final UserAuthorizationRepository userAuthorizationRepository;

    @Autowired
    public UserAuthorizationServiceImpl(UserAuthorizationRepository userAuthorizationRepository) {
        this.userAuthorizationRepository = userAuthorizationRepository;
    }

    @Override
    public void add(UserAuthorize auth) {
        userAuthorizationRepository.JPA.save(auth);
    }
}
