package com.github.yoojia.next.react;

import java.util.concurrent.Callable;

/**
 *
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface Schedule {

    int FLAG_CALLER = 20151010;
    int FLAG_MAIN = 20151111;
    int FLAG_ASYNC = 20151212;

    void submit(Callable<Void> task, int flags) throws Exception;

    void close();
}
