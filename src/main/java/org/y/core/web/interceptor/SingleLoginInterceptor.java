package org.y.core.web.interceptor;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.y.core.annotation.SingleLogin;
import org.y.core.model.entity.User;
import org.y.core.util.Constants;
import org.y.core.web.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户单一登录拦截
 */
@Component
public class SingleLoginInterceptor extends HandlerInterceptorAdapter {

    private static final Map<User, HttpSession> SESSION_MAP = Maps.newHashMap();

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) {
        SingleLogin singleLogin = WebUtil.getHandlerAnnotation(handler, SingleLogin.class);
        if (null == singleLogin) return;
        User currentUser = WebUtil.getSession(Constants.KEY_OF_SESSION_USER);

        // 已登录用户退出登录
        HttpSession session = SESSION_MAP.get(currentUser);
        HttpSession currSession = WebUtil.session();
        if (null != session && currSession != session) {
            try {
                session.removeAttribute(Constants.KEY_OF_SESSION_USER);
                session.setAttribute(Constants.KEY_OF_LOGOUT_REASON, "该账号已在其它地方登录");
            } catch (IllegalStateException ignore) {
                // 如果调用了 session.invalidate()
                // session 将不可用
            }
        }

        // 保留当前登录状态
        SESSION_MAP.put(currentUser, currSession);
    }
}
