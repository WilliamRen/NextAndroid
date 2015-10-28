package com.github.yoojia.next.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-09-19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Event {
    /**
     * Event name
     */
    String value();
}
