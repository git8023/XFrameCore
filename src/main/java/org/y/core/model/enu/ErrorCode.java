package org.y.core.model.enu;

import org.y.core.exception.BusinessException;

public enum ErrorCode {
    SYSTEM_ERROR("系统异常"),
    UN_KNOW_LOGIN_NAME("未知用户名"),
    INVALID_LOGIN_NAME_OR_PASSWORD("用户名或密码错误"),
    EXIST_LOGIN_NAME("已存在用户名"),
    EXIST("数据重复"),
    NOT_LOGIN("用户未登录或登录状态已过期"),
    NOT_EXIST("指定数据不存在"),
    NOT_NULL("缺少必要数据"),
    REPETITIVE_OPERATION("请勿重复操作"),
    DENIED_OPERATION("操作被拒绝"),
    ILLEGAL_OPERATION("非法操作"),
    ILLEGAL_PARAMETER("参数无效"),
    SYSTEM_INITIALIZING("系统初始化中"),
    MAIL_SEND_FAILURE("邮件发送失败"),
    CHECK_CODE_INVALID("验证码无效"),
    CHECK_CODE_EXPIRED("验证码已过期");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 中断当前逻辑, 并抛出异常
     *
     * @param msg 异常消息
     */
    public void breakOff(String msg) {
        throw new BusinessException(this, msg);
    }

    /**
     * 中断当前逻辑, 并抛出异常
     */
    public void breakOff() {
        breakOff(message);
    }
}
