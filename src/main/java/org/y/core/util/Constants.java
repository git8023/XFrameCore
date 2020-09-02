package org.y.core.util;

public interface Constants {
    String KEY_OF_SESSION_USER = StringUtil.genGUID();
    String KEY_OF_LOGOUT_REASON = StringUtil.genGUID();
    String KEY_OF_NOT_FOUND_URI = "KEY_OF_NOT_FOUND_URI";
    String KEY_OF_CHECK_CODE = "KEY_OF_CHECK_CODE";
    String KEY_OF_CHECK_CODE_EXPIRED = "KEY_OF_CHECK_CODE_EXPIRED";

    String VALUE_OF_INITIAL_PASSWORD = StringUtil.getMD5("123456");
    int VALUE_OF_CHECK_CODE_EXPIRED_MINUTES = 5;
}
