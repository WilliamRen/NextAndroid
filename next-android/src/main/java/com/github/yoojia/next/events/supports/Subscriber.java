package com.github.yoojia.next.events.supports;

/**
 * 事件订阅者
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface Subscriber<T> {

    /**
     * 被执行时
     * @param input 输入事件
     * @throws Exception 执行过程中可以抛出异常
     */
    void onCall(T input) throws Exception;

    /**
     * 处理异常
     * @param input 输入事件
     * @param errors 发生的异常
     */
    void onErrors(T input, Exception errors);
}
