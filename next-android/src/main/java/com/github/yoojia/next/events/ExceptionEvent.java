package com.github.yoojia.next.events;

/**
 * Event wrapper for exceptions
 *
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public final class ExceptionEvent {

    public final static String NAME = "com.github.yoojia.next.events.exception#201511231640007";

    public final Exception exception;

    ExceptionEvent(Exception exception) {
        this.exception = exception;
    }

}
