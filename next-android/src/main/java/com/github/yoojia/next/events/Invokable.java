package com.github.yoojia.next.events;

import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
abstract class Invokable<T> {

    public final Meta[] events;
    public final T invokable;
    public final boolean async;

    public Invokable(Meta[] events, T invokable, boolean async) {
        this.events = events;
        this.invokable = invokable;
        this.async = async;
    }

    /**
     * 根据事件参数列表，执行目标
     * @param events 事件参数列表，以 Event-Name: Event-Object 的键值对存在。
     * @throws Exception
     */
    public abstract void invoke(Map<String, Object> events) throws Exception;

    /**
     * 与指定要删除的对象比较，当前对象是否可以被移除
     * @param other 要删除的比较对象
     * @return 是否可以被移除
     */
    public abstract boolean isRemovable(Object other);
}
