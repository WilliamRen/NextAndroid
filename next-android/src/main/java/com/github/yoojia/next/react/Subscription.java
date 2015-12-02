package com.github.yoojia.next.react;

import com.github.yoojia.next.lang.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 事件订阅者封装处理对象
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

    /**
     * 给定一组过滤接口，封装事件订阅接口
     * @param target 事件订阅接口
     * @param scheduleFlags 事件调度标识
     * @param filters 一组过滤接口
     * @param <T> 事件类型
     * @return 封装处理器
     */
    public static <T> Subscription<T> create(Subscriber<T> target, int scheduleFlags, List<Filter<T>> filters) {
        if (filters == null) {
            throw new NullPointerException();
        }
        return new Subscription<>(target, scheduleFlags, filters);
    }

    /**
     * 给定一组过滤接口，封装事件订阅接口
     * @param target 事件订阅接口
     * @param scheduleFlags 事件调度标识
     * @param filters 一组过滤接口
     * @param <T> 事件类型
     * @return 封装处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> Subscription<T> create(Subscriber<T> target, int scheduleFlags, Filter<T>... filters) {
        final List<Filter<T>> filterList = Arrays.asList(filters);
        return new Subscription<>(target, scheduleFlags, filterList);
    }

    /**
     * 给定一个过滤接口，封装事件订阅接口
     * @param target 事件订阅接口
     * @param scheduleFlags 事件调度标识
     * @param filter1 过滤接口
     * @param <T> 事件类型
     * @return 封装处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> Subscription<T> create1(Subscriber<T> target, int scheduleFlags, Filter<T> filter1) {
        return create(target, scheduleFlags, filter1);
    }

    /**
     * 给定2个过滤接口，封装事件订阅接口
     * @param target 事件订阅接口
     * @param scheduleFlags 事件调度标识
     * @param filter1 过滤接口
     * @param filter2 过滤接口
     * @param <T> 事件类型
     * @return 封装处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> Subscription<T> create2(Subscriber<T> target, int scheduleFlags, Filter<T> filter1, Filter<T> filter2) {
        return create(target, scheduleFlags, filter1, filter2);
    }
}
