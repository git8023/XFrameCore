package org.y.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 鉴权注解 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {

  /** 只对登录用户开放 */
  boolean registered() default true;

  /** 排除校验, 只对方法有效 */
  boolean include() default true;
}
