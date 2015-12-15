package com.github.yoojia.next.react;

import com.github.yoojia.next.lang.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 订阅者构建帮助类
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public final class Subscriptions {

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
        return new Subscription<>(target, scheduleFlags, filters == null ? new Filter[0] : filters);
    }

    /**
     * 封装事件订阅接口
     * @param target 事件订阅接口
     * @param scheduleFlags 事件调度标识
     * @param <T> 事件类型
     * @return 封装处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> Subscription<T> create0(Subscriber<T> target, int scheduleFlags) {
        return create(target, scheduleFlags);
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
