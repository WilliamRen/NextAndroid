package com.github.yoojia.next.flux;

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
public @interface Category {
    /**
     * 分类名称
     * @return String
     */
    String value();
}
