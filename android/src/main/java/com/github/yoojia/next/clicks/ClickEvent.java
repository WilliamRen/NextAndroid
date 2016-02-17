package com.github.yoojia.next.clicks;

import android.view.View;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class ClickEvent<T extends View> {

    public final T sender;

    public ClickEvent(T sender) {
        this.sender = sender;
    }
}
