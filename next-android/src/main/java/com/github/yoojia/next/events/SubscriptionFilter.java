package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Filter;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
class SubscriptionFilter<T> implements Filter<Event<T>> {

    private final String mName;
    private final Class<?> mType;

    SubscriptionFilter(String name, Class<?> type) {
        mName = name;
        mType = type;
    }

    @Override
    public boolean accept(Event inputs) {
        if (!mName.equals(inputs.name)) {
            return false;
        }
        if (! mType.equals(inputs.type)) {
            return false;
        }
        return true;
    }
}
