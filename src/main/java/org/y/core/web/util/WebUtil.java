package org.y.core.web.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.y.core.model.enu.ErrorCode;
import org.y.core.util.Constants;
import org.y.core.util.DateUtil;
import org.y.core.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class WebUtil {

    /**
     * 获取请求对象
     *
     * @return 请求对象
     */
    public static HttpServletRequest request() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(sra).getRequest();
    }

    /**
     * 获取响应对象
     *
     * @return 响应对象
     */
    public static HttpServletResponse response() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(sra).getResponse();
    }

    /**
     * 获取会话对象
     *
     * @return 会话对象
     */
    public static HttpSession session() {
        return request().getSession();
    }

    /**
     * 在Session域设置数据
     *
     * @param k   关键字
     * @param v   值
     * @param <T> 值泛型类型
     */
    public static <T> void setSession(String k, T v) {
        session().setAttribute(k, v);
    }

    /**
     * 获取Session域中保存的值
     *
     * @param k   关键字
     * @param <T> 值泛型类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSession(String k) {
        return (T) session().getAttribute(k);
    }

    /**
     * 删除Session域中的值
     *
     * @param k   关键字
     * @param <T> 值泛型类型
     * @return 如果存在值返回保存的值, 否则返回null
     */
    public static <T> T delSession(String k) {
        T ret = getSession(k);
        session().removeAttribute(k);
        return ret;
    }

    /**
     * 获取响应输出流
     *
     * @return 响应输出流
     */
    public static OutputStream getResponseOutputStream() {
        try {
            return response().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置用户到Session域
     *
     * @param user 用户对象
     * @param <T>  用户泛型类型
     */
    public static <T> void setUser(T user) {
        setSession(Constants.KEY_OF_SESSION_USER, user);
    }

    /**
     * 获取Session域中保存的用户
     *
     * @param <T> 用户泛型类型
     * @return 用户对象
     */
    public static <T> T getUser() {
        return getSession(Constants.KEY_OF_SESSION_USER);
    }

    /**
     * 删除Session域中的用户
     */
    public static void delUser() {
        delSession(Constants.KEY_OF_SESSION_USER);
    }

    /**
     * 获取Controller中指定的注解数据, 只针对 {@link HandlerMethod} 有效
     *
     * @param handler       处理器方法
     * @param annotationCls 直接字节码
     * @param <T>           注解泛型类型
     * @return handler不是HandlerMethod总是返回null, 否则在handler中查找指定注解
     */
    public static <T extends Annotation> T getHandlerAnnotation(Object handler, Class<T> annotationCls) {
        return (handler instanceof HandlerMethod)
                ? ((HandlerMethod) handler).getMethodAnnotation(annotationCls)
                : null;
    }

    /**
     * 直接写出文件
     *
     * @param file 文件对象
     */
    public static void writeFile(File file) {
        OutputStream os = WebUtil.getResponseOutputStream();

        if (null == file) {
            try {
                os.write(new byte[]{});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try (FileReader fr = new FileReader(file)) {
            IOUtils.copy(fr, os, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 直接写出文件
     *
     * @param img 文件对象
     */
    public static void writeImage(File img) {
        OutputStream os = WebUtil.getResponseOutputStream();

        if (null == img) {
            try {
                os.write(new byte[]{});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try (InputStream in = new FileInputStream(img)) {
            IOUtils.copy(in, os);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重定向到指定地址
     *
     * @param url 目标地址
     */
    public static void redirect(String url) {
        try {
            response().sendRedirect(url);
        } catch (Exception e) {
            log.warn("重定向[" + url + "]失败: " + e.getMessage());
        }
    }

    /**
     * 转发到指定路径
     *
     * @param url 目标地址
     */
    public static void forward(String url) {
        try {
            request().getRequestDispatcher(url).forward(request(), response());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置验证码
     *
     * @param code           验证码
     * @param expiredMinutes 有效期(分钟)
     */
    public static void setCheckCode(String code, int expiredMinutes) {
        WebUtil.setSession(Constants.KEY_OF_CHECK_CODE, code);
        Date expired = DateUtil.addMinutes(new Date(), expiredMinutes);
        WebUtil.setSession(Constants.KEY_OF_CHECK_CODE_EXPIRED, expired);
    }

    /**
     * 检测验证验证码
     *
     * @param code 验证码
     */
    public static void checkCode(String code) {
        String existCode = WebUtil.getSession(Constants.KEY_OF_CHECK_CODE);
        if (!StringUtil.equals(existCode, code))
            ErrorCode.CHECK_CODE_INVALID.breakOff();

        Date expired = WebUtil.getSession(Constants.KEY_OF_CHECK_CODE_EXPIRED);
        if (new Date().after(expired))
            ErrorCode.CHECK_CODE_EXPIRED.breakOff();
    }

    /**
     * 响应数据类型
     */
    public enum ResponseType {
        HTML {
            public void setContentType() {
                response().setContentType("text/html;charset=utf-8");
            }
        },
        IMAGE {
            public void setContentType() {
                response().setContentType("image/jpeg");
            }
        },
        JSON {
            @Override
            public void setContentType() {
                response().setContentType("application/json");
            }
        };

        public abstract void setContentType();
    }
}
