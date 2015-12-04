package com.github.yoojia.next.react;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
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
     * 指定ExecutorService实现的高度器
     * @param service ExecutorService
     * @return Schedule
     */
    public static Schedule newService(final ExecutorService service) {
        return new Schedule() {

            private final ExecutorService mThreads = service;

            @Override
            public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                Schedules.submit(mThreads, task, scheduleFlags);
            }

        };
    }

    /**
     * 全局共享线程池调度器
     * @return Schedule
     */
    public static Schedule useShared(){
        return new SharedSchedule();
    }

    private static class SharedSchedule implements Schedule {

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

        private static final BlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>();

        public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR
                = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, BLOCKING_QUEUE, THREAD_FACTORY);

        @Override
        public void submit(Callable<Void> task, int flags) throws Exception {
            Schedules.submit(THREAD_POOL_EXECUTOR, task, flags);
        }

    }

    private static void submit(ExecutorService threads, final Callable<Void> task, int scheduleFlags) throws Exception{
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
                    InternalHandler.getDefault().post(new Runnable() {
                        @Override
                        public void run() {
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

    private static class InternalHandler extends Handler{

        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        private static InternalHandler defaultHandler;

        public static InternalHandler getDefault(){
            synchronized (InternalHandler.class) {
                if (defaultHandler == null) {
                    defaultHandler = new InternalHandler();
                }
                return defaultHandler;
            }
        }
    }

}
