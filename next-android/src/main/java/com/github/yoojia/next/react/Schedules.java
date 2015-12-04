package com.github.yoojia.next.react;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    public static Schedule newCaller() {
        return new Schedule() {
            @Override public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                task.call();
            }

            @Override
            public void close() {
                // NOP
            }
        };
    }

    /**
     * 单线程调度器。
     *  - 在测试环境中，使用此调度模式中，空负载时并发处理最快
     * @return Schedule
     */
    public static Schedule newSingleThread() {
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
     * 指定ExecutorService实现的高度器
     * @param service ExecutorService
     * @return Schedule
     */
    public static Schedule newService(final ExecutorService service) {
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

    public static Schedule useShared(){
        return new SharedSchedule();
    }

    private static class SharedSchedule implements Schedule {

        private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
        private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        private static final int KEEP_ALIVE = 1;

        private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {

            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "SharedThread #" + mCount.getAndIncrement());
            }
        };

        private static final BlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<Runnable>(128);

        public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR
                = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, BLOCKING_QUEUE, THREAD_FACTORY);

        @Override
        public void submit(Callable<Void> task, int flags) throws Exception {
            run(THREAD_POOL_EXECUTOR, MAIN_HANDLER, task, flags);
        }

        @Override
        public void close() {
            // NOP
        }
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
                runOnMain(mainHandler, task);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Schedule flags: " + scheduleFlags);
        }
    }

    private static void runOnMain(Handler mainHandler, final Callable<Void> task) throws Exception{
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
    }
}
