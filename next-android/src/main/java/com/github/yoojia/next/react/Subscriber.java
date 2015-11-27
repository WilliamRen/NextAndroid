package com.github.yoojia.next.react;

/**
 *
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface Subscriber<T> {

    void onCall(T input) throws Exception;

    void onErrors(T input, Exception errors);
}
