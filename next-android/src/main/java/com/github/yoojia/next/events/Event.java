package com.github.yoojia.next.events;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
public class Event<T> {

    public final String name;
    public final T value;
    final Class<?> type;

    Event(String name, T value) {
        this.name = name;
        this.value = value;
        final Class<?> type = value.getClass();
        this.type = type.isPrimitive() ? Primitives.getWrap(type) : type;
    }
}
