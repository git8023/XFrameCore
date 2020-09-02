package org.y.core.model.result;

import lombok.Data;
import org.y.core.model.enu.ErrorCode;
import org.y.core.util.StringUtil;

@Data
public class Result {
    private boolean flag;
    private String message;
    private Object data;
    private ErrorCode errorCode;

    public Result(boolean flag, String message, Object data, ErrorCode errorCode) {
        this.flag = flag;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        if (StringUtil.isBlank(message) && (null != errorCode)) this.message = errorCode.getMessage();
    }

    public static Result error(ErrorCode errorCode) {
        return new Result(false, null, null, errorCode);
    }

    public static Result error(ErrorCode errorCode, String message) {
        return new Result(false, message, null, errorCode);
    }

    public static Result fail(String message) {
        return new Result(false, message, null, null);
    }

    public static Result data(Object data) {
        return new Result(true, null, data, null);
    }

    public static Result success() {
        return data(null);
    }
}
