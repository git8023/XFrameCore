package org.y.core.service;

import org.y.core.model.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 查询指定用户名是否存在
     *
     * @param username 用户名
     * @return true-存在, false-不存在
     */
    boolean existByUserName(String username);

    /**
     * 校验指定邮箱是否存在
     *
     * @param email 邮箱
     * @return true-存在, false-不存在
     */
    boolean existByEmail(String email);

    /**
     * 发送邮件验证码
     *
     * @param email 邮箱
     * @return 验证码
     */
    String sendCheckCode(String email);

    /**
     * 发送邮件验证码
     *
     * @param eou email or username
     * @return 通过username或者email成功找到已注册用户并发送邮件成功, 返回验证码
     */
    String sendCheckCode2(String eou);

    /**
     * 用户注册
     *
     * @param user 用户资料
     */
    void reg(User user);

    /**
     * 登录
     *
     * @param username 用户名|邮箱
     * @param password 密码
     * @return 登录成功返回用户信息, 否则抛出异常
     */
    User login(String username, String password);

    /**
     * 修改密码
     *
     * @param eou    email or username
     * @param newPwd 新密码
     */
    void updatePwd(String eou, String newPwd);
}
