package com.github.yoojia.next.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
    /**
     * 是否异步执行（在非UI线程上执行）。默认为False，在UI线程中执行。
     */
    boolean async() default false;
}
