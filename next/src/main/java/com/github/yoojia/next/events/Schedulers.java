package com.github.yoojia.next.events;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class Schedulers {

    public static ExecutorService fixed(int count) {
        int threads = count <= 0 ? 1 : count;
        return Executors.newFixedThreadPool(threads * 2);
    }

    public static ExecutorService processors(){
        return fixed(Runtime.getRuntime().availableProcessors());
    }

    public static ExecutorService cached(){
        return Executors.newCachedThreadPool();
    }
}
