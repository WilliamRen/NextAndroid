package com.github.yoojia.next.events.supports;

import com.github.yoojia.next.lang.Filter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 事件订阅描述对象，封装事件订阅处理都及调度标识
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public class Descriptor<T> {

    private static final int COUNT_OF_PER_SUBSCRIBER_MAY_HAS_FILTERS = 2;

    final Subscriber<T> subscriber;
    final int scheduleFlag;

    private final ArrayList<Filter<T>> mFilters = new ArrayList<>(COUNT_OF_PER_SUBSCRIBER_MAY_HAS_FILTERS);

    Descriptor(Subscriber<T> subscriber, int scheduleFlag, Filter<T>[] filters) {
        this.subscriber = subscriber;
        this.scheduleFlag = scheduleFlag;
        if (filters.length <= 0) {
            throw new IllegalArgumentException("Filter is required!");
        }
        mFilters.addAll(Arrays.asList(filters));
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
