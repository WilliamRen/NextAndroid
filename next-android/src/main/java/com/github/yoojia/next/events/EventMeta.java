package com.github.yoojia.next.events;

import com.github.yoojia.next.lang.Primitives;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
public class EventMeta {

    public final String name;
    public final Object value;
    final Class<?> type;

    EventMeta(String name, Object value) {
        this.name = name;
        this.value = value;
        final Class<?> type = value.getClass();
        this.type = type.isPrimitive() ? Primitives.getWrapClass(type) : type;
    }

    @Override
    public String toString() {
        return "{name=" + name +  ", value=" + value + "}";
    }

    static EventMeta with(String name, Object value) {
        return new EventMeta(name, value);
    }
}
