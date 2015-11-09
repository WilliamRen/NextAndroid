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

    public static Schedule caller() {
        return new Schedule() {
            @Override public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                if (Schedule.CALLER != scheduleFlags) {
                    throw new IllegalArgumentException("Unsupported flags(Schedule.MAIN/ASYNC) in CALLER Schedule !");
                }
                task.call();
            }

            @Override
            public void close() {/*nop*/}
        };
    }

    public static Schedule singleThread() {
        return new Schedule() {

            private final Handler mMainHandler = new Handler(Looper.getMainLooper());
            private final ExecutorService mThread = Executors.newSingleThreadExecutor();

            @Override
            public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                run(mThread, mMainHandler, task, scheduleFlags);
            }

            @Override
            public void close() {
                mThread.shutdown();
            }
        };
    }

    public static Schedule threads(final int threads) {
        return new Schedule() {

            private final Handler mMainHandler = new Handler(Looper.getMainLooper());
            private final ExecutorService mThread = Executors.newFixedThreadPool(threads);

            @Override
            public void submit(Callable<Void> task, int scheduleFlags) throws Exception {
                run(mThread, mMainHandler, task, scheduleFlags);
            }

            @Override
            public void close() {
                mThread.shutdown();
            }
        };
    }

    private static void run(ExecutorService threads, Handler mainHandler, final Callable<Void> task, int scheduleFlags) throws Exception{
        if (Schedule.ASYNC == scheduleFlags) {
            threads.submit(task);
        }else if (Schedule.CALLER == scheduleFlags){
            task.call();
        }else if (Schedule.MAIN == scheduleFlags){
            mainHandler.post(new Runnable() {
                @Override public void run() {
                    try {
                        task.call();
                    } catch (Exception err) {
                        throw new RuntimeException(err);
                    }
                }
            });
        }else {
            throw new IllegalArgumentException("Unsupported Schedule flags: " + scheduleFlags);
        }
    }
}
