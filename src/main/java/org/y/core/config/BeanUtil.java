package org.y.core.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtil.applicationContext = applicationContext;
    }

    /**
     * 按名称获取对象
     *
     * @param name 名称
     * @return Bean对象
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 按类型字节码获取对象
     *
     * @param aClass 类型字节码
     * @param <T>    泛型类型
     * @return Bean对象
     */
    public static <T> T getBean(Class<T> aClass) {
        return applicationContext.getBean(aClass);
    }

    /**
     * 按名称和类型字节码获取对象
     *
     * @param name   名称
     * @param aClass 类型字节码
     * @param <T>    泛型类型
     * @return Bean对象
     */
    public static <T> T getBean(String name, Class<T> aClass) {
        return applicationContext.getBean(name, aClass);
    }
}
