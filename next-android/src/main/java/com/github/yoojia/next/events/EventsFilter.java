package com.github.yoojia.next.events;

import com.github.yoojia.next.lang.ClassTypes;
import com.github.yoojia.next.lang.Filter;

/**
 * Subscriber 事件过滤器
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
class EventsFilter implements Filter<Meta> {

    private final String mDefineName;
    private final Class<?> mDefineType;

    EventsFilter(String defineName, Class<?> rawDefineType) {
        mDefineName = defineName;
        mDefineType = ClassTypes.wrap(rawDefineType);
    }

    @Override
    public boolean accept(Meta evt) {
        // 不接受: 事件名不同
        if (! mDefineName.equals(evt.name)) {
            return false;
        }
        // 不接受: 事件类型不相同
        if (! mDefineType.equals(evt.type)) {
            return false;
        }
        return true;
    }

    static EventsFilter with(String defineName, Class<?> rawDefineType) {
        return new EventsFilter(defineName, rawDefineType);
    }
}
