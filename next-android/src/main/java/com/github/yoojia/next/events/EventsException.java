package com.github.yoojia.next.events;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public final class EventsException extends RuntimeException{

    private EventsException(Throwable origin) {
        super(origin.getCause());
    }

    static EventsException recatch(Exception exception){
        return new EventsException(exception);
    }

}
