package com.github.yoojia.next.clicks;

import android.view.KeyEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.FIELD)
public @interface ClickEvt {

    /**
     * 事件名
     * @return String
     */
    String value() default "";

    /**
     * 按键码
     * @return Int
     */
    int keyCode() default KeyEvent.KEYCODE_UNKNOWN;
}
