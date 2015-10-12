package com.github.yoojia.next.views;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoView {
    int value();
    int[] parents() default {};
}
