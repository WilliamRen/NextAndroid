package com.github.yoojia.next.events.supports;

/**
 * Emit Miss
 *
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface OnEventsListener<T> {

    void onWithoutSubscriber(T input);
}
