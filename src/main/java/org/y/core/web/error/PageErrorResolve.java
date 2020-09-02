package org.y.core.web.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 页面请求错误处理
 */
@Slf4j
@Component
public class PageErrorResolve implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.warn(ex.getMessage(), ex);
        if (ex instanceof ClientAbortException) {
            log.warn("捕获到 ClientAbortException ");
            // 客户端出现问题, 不再返回数据(页面)
            return null;
        }
        return new ModelAndView("/").addObject("message", "出错了");
    }

}
