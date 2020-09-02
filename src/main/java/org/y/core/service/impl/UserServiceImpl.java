package org.y.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.y.core.model.entity.User;
import org.y.core.model.enu.ErrorCode;
import org.y.core.repository.UserRepository;
import org.y.core.service.MailService;
import org.y.core.service.UserService;
import org.y.core.util.StringUtil;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public boolean existByUserName(String username) {
        return null != userRepository.JPA.findByUsername(username);
    }

    @Override
    public boolean existByEmail(String email) {
        return null != userRepository.JPA.findByEmail(email);
    }

    @Override
    public String sendCheckCode(String email) {
        String code = StringUtil.randomNumber(6);
        mailService.sendSimpleMail(email, "XFrame验证码", "本次注册验证码为: " + code + "\n\n5分钟内有效\n\n如非本人操作请忽略！");
        return code;
    }

    @Override
    public String sendCheckCode2(String eou) {
        User user = getUserByEOU(eou);
        if (null == user)
            ErrorCode.NOT_EXIST.breakOff("指定用户名或邮箱不存在");

        String email = user.getEmail();
        return sendCheckCode(email);
    }

    @Override
    public void reg(User user) {
        boolean existEmail = existByEmail(user.getEmail());
        if (existEmail)
            ErrorCode.EXIST.breakOff("指定邮箱已存在, 请更换.");

        boolean existUsername = existByUserName(user.getUsername());
        if (existUsername)
            ErrorCode.EXIST.breakOff("指定账户已存在, 请更换.");

        user.setCreateDate(new Date());
        userRepository.JPA.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = getUserByEOU(username);
        if (null == user || StringUtil.equals(password, user.getPassword()))
            ErrorCode.INVALID_LOGIN_NAME_OR_PASSWORD.breakOff();

        user.setPassword(null);
        return user;
    }

    @Override
    public void updatePwd(String eou, String newPwd) {
        if (StringUtil.isBlank(newPwd))
            ErrorCode.ILLEGAL_PARAMETER.breakOff();

        User user = getUserByEOU(eou);
        if (null == user)
            ErrorCode.ILLEGAL_OPERATION.breakOff();

        user.setPassword(newPwd);
        userRepository.JPA.save(user);
    }

    /**
     * 获取用户数据
     *
     * @param eou email or username
     * @return 获取成功返回用户数据, 否则返回null
     */
    private User getUserByEOU(String eou) {
        User user = userRepository.JPA.findByUsername(eou);
        if (null == user)
            user = userRepository.JPA.findByEmail(eou);
        return user;
    }
}
