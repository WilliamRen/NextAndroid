package com.github.yoojia.next.react;

/**
 *
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface Filter<T> {

    boolean accept(T input);
}
