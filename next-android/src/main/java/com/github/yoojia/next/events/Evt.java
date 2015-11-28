package com.github.yoojia.next.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
@Inherited
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Evt {
    /**
     * 事件名称
     * @return String
     */
    String value();
}
