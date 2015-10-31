package com.github.yoojia.next.events;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public class Schedulers {

    /**
     * 单个线程
     */
    public static Schedulers single() {
        return new Schedulers(Executors.newSingleThreadExecutor());
    }

    /**
     * 非异步的回调操作在主线程中执行, 异步回调操作由线程池来执行.
     */
    public static Schedulers main() {
        return main(Runtime.getRuntime().availableProcessors());
    }

    public static Schedulers mainSingle() {
        return main(Executors.newSingleThreadExecutor());
    }

    /**
     * 非异步的回调操作在主线程中执行, 异步回调操作由线程池来执行.
     * @param threads 指定异步线程池的线程数量
     */
    public static Schedulers main(int threads) {
        return main(Executors.newFixedThreadPool(threads));
    }

    /**
     * 非异步的回调操作在主线程中执行, 异步回调操作由线程池来执行.
     * @param threads 指定线程池
     */
    public static Schedulers main(ExecutorService threads) {
        return
        new Schedulers(threads) {

            private final Handler mMainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void submit(final Callable<Void> task, boolean async) throws Exception {
                if (async) {
                    threads.submit(task);
                }else{
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                task.call();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        };
    }

    public static Schedulers threads() {
        return threads(Runtime.getRuntime().availableProcessors());
    }

    public static Schedulers threads(int threads) {
        return threads(Executors.newFixedThreadPool(threads));
    }

    public static Schedulers threads(ExecutorService threads) {
        return
        new Schedulers(threads) {
            @Override
            public void submit(final Callable<Void> task, boolean async) throws Exception {
                if (async) {
                    threads.submit(task);
                }else{
                    try {
                        task.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }

    protected Schedulers(ExecutorService threads) {
        this.threads = threads;
    }

    protected final ExecutorService threads;

    public void submit(Callable<Void> task, boolean async) throws Exception {
        if (async) {
            threads.submit(task);
        }else{
            task.call();
        }
    }

    public void submitSilently(Callable<Void> task, boolean async) {
        try {
            submit(task, async);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        threads.shutdown();
    }

}
