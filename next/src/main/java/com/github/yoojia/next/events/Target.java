package com.github.yoojia.next.events;

import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
public interface Target {

    /**
     * 根据事件参数列表，执行目标
     * @param events 事件参数列表，以 Event-Name: Event-Object 的键值对存在。
     * @throws Exception
     */
    void invoke(Map<String, Object> events) throws Exception;

    /**
     * 是否同一个目标对象实例
     * @param host 传来以检查的对象实例
     * @return 是否相同
     */
    boolean isSameHost(Object host);
}
