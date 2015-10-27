package com.github.yoojia.next.events;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class Schedulers {

    public static final ExecutorService Processor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static final ExecutorService Cached = Executors.newCachedThreadPool();
}
