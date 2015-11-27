package com.github.yoojia.next.react;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public class Schedules {

    /**
     * 调用者
     * @return Schedule
     */
    public static Schedule caller() {
        return new Schedule() {
            @Override public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                if (Schedule.FLAG_ON_CALLER != scheduleFlags) {
                    throw new IllegalArgumentException(
                            "Unsupported flags(Schedule.FLAG_ON_MAIN/Schedule.FLAG_ON_THREADS) in CALLER Schedule !");
                }
                task.call();
            }

            @Override
            public void close() {/*nop*/}
        };
    }

    /**
     * 单线程
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
     * 固定线程数
     * @return Schedule
     */
    public static Schedule threads(final int threads) {
        return new Schedule() {

            private final Handler mMainHandler = new Handler(Looper.getMainLooper());
            private final ExecutorService mThreads = Executors.newFixedThreadPool(threads);

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
                mainHandler.post(new Runnable() {
                    @Override public void run() {
                        try {
                            task.call();
                        } catch (Exception err) {
                            throw new RuntimeException(err);
                        }
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("Unsupported Schedule flags: " + scheduleFlags);
        }
    }
}
