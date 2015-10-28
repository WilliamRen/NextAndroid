package com.github.yoojia.next.events;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class Threads {

    public static ExecutorService fixed(int count) {
        if (count <= 0) {
            return Executors.newSingleThreadExecutor();
        }else{
            return Executors.newFixedThreadPool(count);
        }
    }

    public static ExecutorService single(){
        return Executors.newSingleThreadExecutor();
    }

    public static ExecutorService CPUx2(){
        return fixed(Runtime.getRuntime().availableProcessors() * 2);
    }

    public static ExecutorService CPU(){
        return fixed(Runtime.getRuntime().availableProcessors());
    }

    public static ExecutorService cached(){
        return Executors.newCachedThreadPool();
    }
}
