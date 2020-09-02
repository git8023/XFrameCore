package org.y.core.util;

import org.y.core.exception.BusinessException;
import org.y.core.model.entity.User;
import org.y.core.model.enu.ErrorCode;

/**
 * 业务断言工具
 */
public class BusinessAssert {

    public static void exist(boolean isThrow, String message) {
        throwCode(isThrow, ErrorCode.EXIST, message);
    }

    public static void checkLogin(User user) {
        throwCode(null == user || 0 == user.getId(), ErrorCode.NOT_LOGIN);
    }

    public static void notExist(Object o, String message) {
        throwCode(null == o, ErrorCode.NOT_EXIST, message);
    }

    public static void notNull(Object o, String message) {
        throwCode(null == o, ErrorCode.NOT_NULL, message);
    }

    public static void illegalOperation(boolean isThrow, String message) {
        throwCode(isThrow, ErrorCode.ILLEGAL_OPERATION, message);
    }

    private static void throwCode(boolean isThrow, ErrorCode errorCode) {
        throwCode(isThrow, errorCode, null);
    }

    private static void throwCode(boolean isThrow, ErrorCode errorCode, String message) {
        if (isThrow) throw new BusinessException(errorCode, message);
    }
}
