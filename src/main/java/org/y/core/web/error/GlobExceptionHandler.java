package org.y.core.web.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.y.core.exception.BusinessException;
import org.y.core.model.enu.ErrorCode;
import org.y.core.model.result.Result;
import org.y.core.util.CollectionUtil;

import java.util.List;

/**
 * 逻辑异常处理
 */
@Slf4j
@ControllerAdvice("org.y.core")
public class GlobExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result errorJson(Exception ex) {
        log.warn(ex.getMessage(), ex);
        return Result.fail("服务器忙, 请稍候再试");
    }

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result businessException(BusinessException be) {
        ErrorCode errorCode = be.getErrorCode();
        return Result.error(errorCode, be.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result validationException(BindException cve) {
        List<ObjectError> oes = cve.getAllErrors();
        if (CollectionUtil.isNotEmpty(oes) && null != oes.get(0))
            return Result.error(ErrorCode.ILLEGAL_PARAMETER, oes.get(0).getDefaultMessage());
        return Result.error(ErrorCode.ILLEGAL_PARAMETER, cve.getMessage());
    }

    @ExceptionHandler(ClientAbortException.class)
    @ResponseBody
    public void clientAbortException(ClientAbortException cae) {
        log.warn("捕获到 ClientAbortException:" + cae.getMessage());
    }
}
