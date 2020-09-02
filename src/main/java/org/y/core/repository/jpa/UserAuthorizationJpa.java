package org.y.core.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y.core.model.entity.UserAuthorize;

@Repository
public interface UserAuthorizationJpa extends JpaRepository<UserAuthorize, Integer> {
}
