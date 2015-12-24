package com.github.yoojia.next.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    /**
     * 订阅事件
     * @return String
     */
    String on();

    /**
     * 指定回调方式。
     * - 默认方式为 CALLER；
     * @return RunTypes
     */
    RunTypes run() default RunTypes.CALLER;
}
