package org.y.core.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.core.model.entity.User;
import org.y.core.model.enu.ErrorCode;
import org.y.core.model.result.Result;
import org.y.core.service.UserService;
import org.y.core.util.Constants;
import org.y.core.web.util.WebUtil;

/**
 * 用户控制器, 普通用户注册/编辑基本资料
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询指定用户名是否已经存在
     *
     * @param username 用户名
     */
    @RequestMapping("/existUsername/{username}")
    public Result existUsername(@PathVariable String username) {
        boolean exist = userService.existByUserName(username);
        return Result.data(exist);
    }

    /**
     * 查询指定用户名是否已经存在
     *
     * @param email 用户名
     */
    @RequestMapping("/existEmail/{email}")
    public Result existEmail(@PathVariable String email) {
        boolean exist = userService.existByEmail(email);
        return Result.data(exist);
    }

    /**
     * 发送验证码
     *
     * @param user 接受Email
     */
    @RequestMapping("/checkCode")
    public Result checkCode(User user) {
        String email = user.getEmail();
        String code = userService.sendCheckCode(email);
        WebUtil.setCheckCode(code, Constants.VALUE_OF_CHECK_CODE_EXPIRED_MINUTES);
        return Result.success();
    }

    /**
     * 注册
     *
     * @param user      注册信息
     * @param emailCode 邮箱校验码
     */
    @RequestMapping("/reg")
    public Result reg(User user, String emailCode) {
        WebUtil.checkCode(emailCode);
        userService.reg(user);
        return Result.success();
    }

    /**
     * 登录
     *
     * @param username 登录名|邮箱
     * @param password 密码
     */
    @RequestMapping("/login")
    public Result login(String username, String password) {
        User user = userService.login(username, password);
        WebUtil.setUser(user);
        return Result.data(user.getId());
    }

    /**
     * 发送验证码
     *
     * @param username 用户名或者邮箱
     */
    @RequestMapping("/checkCodeEmailOrUsername")
    public Result checkCodeMailOrUsername(String username) {
        String code = userService.sendCheckCode2(username);
        WebUtil.setCheckCode(code, 5);
        return Result.success();
    }

    /**
     * 重置密码
     *
     * @param username  邮箱
     * @param password  新密码
     * @param emailCode 校验码
     */
    @RequestMapping("/resetPwd")
    public Result resetPwd(String username, String password, String emailCode) {
        WebUtil.checkCode(emailCode);
        userService.updatePwd(username, password);
        return Result.success();
    }

    /**
     * 获取当前登录用户id
     */
    @RequestMapping("/sid")
    public Result SignedUserId() {
        User user = WebUtil.getUser();
        if (null == user) ErrorCode.NOT_LOGIN.breakOff();
        return Result.data(user.getId());
    }
}
