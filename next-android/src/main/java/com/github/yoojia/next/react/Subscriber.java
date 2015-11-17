package com.github.yoojia.next.react;

/**
 *
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface Subscriber<T> {

    void call(T input) throws Exception;

    void errors(Exception errors);
}
