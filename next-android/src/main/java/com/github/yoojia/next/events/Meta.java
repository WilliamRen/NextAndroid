package com.github.yoojia.next.events;

import com.github.yoojia.next.lang.ClassTypes;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
public class Meta {

    public final String name;
    public final Object value;
    final Class<?> type;

    Meta(String name, Object value) {
        this.name = name;
        this.value = value;
        final Class<?> type = value.getClass();
        this.type = ClassTypes.wrap(type);
    }

    @Override
    public String toString() {
        return "{name=" + name +  ", value=" + value + "}";
    }

    static Meta with(String name, Object value) {
        return new Meta(name, value);
    }
}
