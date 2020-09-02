package org.y.core.service;

import org.y.core.model.entity.UserAuthorize;

/**
 * 用户授权服务接口
 */
public interface UserAuthorizationService {

    /**
     * 添加授权信息
     *
     * @param auth 授权信息
     */
    void add(UserAuthorize auth);

}
