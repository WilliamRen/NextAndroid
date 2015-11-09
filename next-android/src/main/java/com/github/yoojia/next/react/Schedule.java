package com.github.yoojia.next.react;

import java.util.concurrent.Callable;

/**
 *
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface Schedule {

    int CALLER = 0;
    int MAIN = 1;
    int ASYNC = 2;

    void submit(Callable<Void> task, int flags) throws Exception;

    void close();
}
