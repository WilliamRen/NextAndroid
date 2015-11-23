package com.github.yoojia.next.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-06
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    /**
     * 使用 onThreads 替代
     * @return Boolean
     */
    @Deprecated
    boolean async() default false;

    /**
     * 是否在其它线程（非主线程）中回调
     * @return Boolean
     */
    boolean onThreads() default false;
}
