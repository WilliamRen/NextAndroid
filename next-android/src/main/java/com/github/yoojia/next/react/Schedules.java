package com.github.yoojia.next.react;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务调度器
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public class Schedules {

    /**
     * 调用者调试器，忽略 Schedule.FLAG_X 参数。
     * 由调用者执行最终回调目标。
     * @return Schedule
     */
    public static Schedule caller() {
        return new Schedule() {
            @Override public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                task.call();
            }

            @Override
            public void close() {/*nop*/}
        };
    }

    /**
     * 匿名线程调度器，允许最大并发为CPU数量。
     * - 超过最大并发数，添加任务时将被阻塞。每次执行任务时创建一个匿名线程，不适用于大并发任务。
     * - 此调度器为NextClickProxy等存在不调用close()/destroy()方法而调试。
     * @return Schedule
     */
    public static Schedule anonymous(){
        return new Schedule() {

            private final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

            private final Handler mMainHandler = new Handler(Looper.getMainLooper());
            private final AtomicInteger mThreads = new AtomicInteger(0);

            @Override
            public void submit(final Callable<Void> task, int scheduleFlags) throws Exception {
                final Runnable runnable = new Runnable() {
                    @Override public void run() {
                        try {
                            task.call();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }finally {
                            synchronized (mThreads) {
                                mThreads.notify();
                            }
                        }
                    }
                };
                switch (scheduleFlags) {
                    case Schedule.FLAG_ON_THREADS:
                        if (mThreads.get() >= MAX_THREADS) {
                            synchronized (mThreads) {
                                mThreads.wait();
                            }
                        }
                        mThreads.incrementAndGet();
                        startAnonymous(runnable);
                        break;
                    case Schedule.FLAG_ON_CALLER:
                        task.call();
                        break;
                    case Schedule.FLAG_ON_MAIN:
                        if (Looper.getMainLooper() == Looper.myLooper()) {
                            task.call();
                        }else{
                            mMainHandler.post(runnable);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported Schedule flags: " + scheduleFlags);
                }
            }

            @Override
            public void close() {
                // may not call
            }

            private void startAnonymous(Runnable runnable) {
                final Thread thread = new Thread(runnable, "anonymous-schedule");
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
            }
        };
    }

    /**
     * 单线程调度器。
     *  - 在测试环境中，使用此调度模式中，空负载时并发处理最快
     * @return Schedule
     */
    public static Schedule singleThread() {
        return new Schedule() {

            private final Handler mMainHandler = new Handler(Looper.getMainLooper());
            private final ExecutorService mThreads = Executors.newSingleThreadExecutor();

            @Override
            public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                run(mThreads, mMainHandler, task, scheduleFlags);
            }

            @Override
            public void close() {
                mThreads.shutdown();
            }
        };
    }

    /**
     * 固定线程数调度器
     * @return Schedule
     */
    public static Schedule threads(final int threads) {
        return service(Executors.newFixedThreadPool(threads));
    }

    /**
     * 指定ExecutorService实现的高度器
     * @param service ExecutorService
     * @return Schedule
     */
    public static Schedule service(final ExecutorService service) {
        return new Schedule() {

            private final Handler mMainHandler = new Handler(Looper.getMainLooper());
            private final ExecutorService mThreads = service;

            @Override
            public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                run(mThreads, mMainHandler, task, scheduleFlags);
            }

            @Override
            public void close() {
                mThreads.shutdown();
            }
        };
    }

    private static void run(ExecutorService threads, Handler mainHandler, final Callable<Void> task, int scheduleFlags) throws Exception{
        switch (scheduleFlags) {
            case Schedule.FLAG_ON_THREADS:
                threads.submit(task);
                break;
            case Schedule.FLAG_ON_CALLER:
                task.call();
                break;
            case Schedule.FLAG_ON_MAIN:
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    task.call();
                }else{
                    mainHandler.post(new Runnable() {
                        @Override public void run() {
                            try {
                                task.call();
                            } catch (Exception err) {
                                throw new RuntimeException(err);
                            }
                        }
                    });
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported Schedule flags: " + scheduleFlags);
        }
    }
}
