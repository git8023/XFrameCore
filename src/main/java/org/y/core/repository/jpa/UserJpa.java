package org.y.core.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y.core.model.entity.User;

@Repository
public interface UserJpa extends JpaRepository<User, Integer> {

    /**
     * 指定用户名查询用户数据
     *
     * @param username 用户名
     * @return 用户数据
     */
    User findByUsername(String username);

    /**
     * 指定邮箱查询用户数据
     *
     * @param email 邮箱
     * @return 用户数据
     */
    User findByEmail(String email);

    /**
     * 指定ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(int id);

}
