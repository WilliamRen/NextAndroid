package com.github.yoojia.next.events;

import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
abstract class Subscriber {

    /**
     * 根据事件参数列表，执行目标
     * @param events 事件参数列表，以 Event-Name: Event-Object 的键值对存在。
     * @throws Exception
     */
    public abstract void notify(Map<String, Object> events) throws Exception;

    /**
     * 是否同一个目标对象实例
     * @param host 传来用于检查的对象实例引用
     * @return 是否相同
     */
    public abstract boolean isSameHost(Object host);
}
