package com.github.yoojia.next.events;

import com.github.yoojia.next.lang.Primitives;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class EventWrap {

    public final String event;
    public final Class<?> type;

    public EventWrap(String event, Class<?> type) {
        this.event = event;
        this.type = type.isPrimitive() ? Primitives.getWrapClass(type) : type;
    }
}
