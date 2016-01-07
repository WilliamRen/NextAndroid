package com.github.yoojia.next.events.supports;

import com.github.yoojia.next.lang.Filter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 事件订阅者封装处理对象
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public class Subscription<T> {

    private static final int COUNT_OF_PER_SUBSCRIBER_MAY_HAS_FILTERS = 2;

    final Subscriber<T> target;
    final int scheduleFlag;

    private final ArrayList<Filter<T>> mFilters = new ArrayList<>(COUNT_OF_PER_SUBSCRIBER_MAY_HAS_FILTERS);

    Subscription(Subscriber<T> target, int scheduleFlag, Filter<T>[] filters) {
        this.target = target;
        this.scheduleFlag = scheduleFlag;
        if (filters.length != 0) {
            mFilters.addAll(Arrays.asList(filters));
        }
    }

    /* hide for Reactor */
    boolean accept(T input) {
        for (Filter<T> filter : mFilters) {
            if ( ! filter.accept(input)) {
                return false;
            }
        }
        return true;
    }

}
