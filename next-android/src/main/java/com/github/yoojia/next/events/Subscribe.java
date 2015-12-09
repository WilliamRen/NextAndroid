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
     * 指定回调方式。
     * - 默认方式为 CALLER；
     * @return RunOn
     */
    RunOn runOn() default RunOn.CALLER;
}
