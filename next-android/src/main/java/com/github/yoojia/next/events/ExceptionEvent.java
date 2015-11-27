package com.github.yoojia.next.events;

/**
 * Event wrapper for exceptions
 *
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public final class ExceptionEvent {

    public final static String NAME = "com.github.yoojia.next.events.exception#201511231640007";

    /**
     * 异常内容
     */
    public final Exception exception;

    /**
     * 发生异常的方法名
     */
    public final String methodName;

    /**
     * 发生异常的事件名称
     */
    public final String eventName;

    /**
     * 发生异常的事件对象
     */
    public final Object eventObject;

    ExceptionEvent(Exception exception, String methodName, String eventName, Object eventObject) {
        this.exception = exception;
        this.methodName = methodName;
        this.eventName = eventName;
        this.eventObject = eventObject;
    }

}
