package com.github.yoojia.next.events;

import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.lang.Primitives;

/**
 * Subscriber 事件过滤器
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
class EventsFilter implements Filter<EventMeta> {

    private final String mDefineName;
    private final Class<?> mDefineType;

    EventsFilter(String defineName, Class<?> rawDefineType) {
        mDefineName = defineName;
        mDefineType = rawDefineType.isPrimitive() ? Primitives.getWrapClass(rawDefineType) : rawDefineType;
    }

    @Override
    public boolean accept(EventMeta evt) {
        // 不接受: 事件名不同
        if (!mDefineName.equals(evt.name)) {
            return false;
        }
        // 不接受: 事件类型不相同
        if (! mDefineType.equals(evt.type)) {
            return false;
        }
        return true;
    }
}