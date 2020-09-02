package org.y.core.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.y.core.annotation.Authentication;
import org.y.core.exception.BusinessException;
import org.y.core.model.entity.User;
import org.y.core.util.BusinessAssert;
import org.y.core.util.Constants;
import org.y.core.web.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录状态拦截
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        if (!(handler instanceof HandlerMethod)) return true;

        HandlerMethod method = (HandlerMethod) handler;
        Authentication authM = method.getMethodAnnotation(Authentication.class);
        Class<?> type = method.getMethod().getDeclaringClass();
        Authentication authT = type.getAnnotation(Authentication.class);

        // 类和方法都没有注解
        if (null == authT && null == authM) return true;

        // 1. 类注解, 没有方法注解
        // 2. 类注解, 方法有注解(exclude==false)
        boolean checkAuth = (null != authT) && ((null == authM) || authM.include());
        if (checkAuth) {
            User user = WebUtil.getUser();
            try {
                BusinessAssert.checkLogin(user);
            } catch (BusinessException e) {
                String reason = WebUtil.delSession(Constants.KEY_OF_LOGOUT_REASON);
                if (null != reason) {
                    BusinessException businessException = new BusinessException(e.getErrorCode(), reason);
                    businessException.setStackTrace(e.getStackTrace());
                    throw businessException;
                }
                throw e;
            }
        }

        return true;
    }
}
