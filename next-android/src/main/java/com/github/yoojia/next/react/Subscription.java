package com.github.yoojia.next.react;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public class Subscription<T> {

    final Subscriber<T> target;
    final int targetScheduleFlags;

    private final List<Filter<T>> mFilters = new ArrayList<>();

    private Subscription(Subscriber<T> target, int scheduleFlags, List<Filter<T>> filters) {
        this.target = target;
        this.targetScheduleFlags = scheduleFlags;
        mFilters.addAll(filters);
    }

    boolean filter(T input) {
        for (Filter<T> filter : mFilters) {
            if ( ! filter.accept(input)) {
                return false;
            }
        }
        return true;
    }

    public static <T> Subscription<T> create(Subscriber<T> target, int scheduleFlags, List<Filter<T>> filters) {
        if (filters == null) {
            throw new NullPointerException();
        }
        return new Subscription<>(target, scheduleFlags, filters);
    }

    @SuppressWarnings("unchecked")
    public static <T> Subscription<T> create(Subscriber<T> target, int scheduleFlags, Filter<T>... filters) {
        final List<Filter<T>> filterList = Arrays.asList(filters);
        return new Subscription<>(target, scheduleFlags, filterList);
    }

    @SuppressWarnings("unchecked")
    public static <T> Subscription<T> create1(Subscriber<T> target, int scheduleFlags, Filter<T> filter1) {
        return create(target, scheduleFlags, filter1);
    }

    @SuppressWarnings("unchecked")
    public static <T> Subscription<T> create2(Subscriber<T> target, int scheduleFlags, Filter<T> filter1, Filter<T> filter2) {
        return create(target, scheduleFlags, filter1, filter2);
    }
}
