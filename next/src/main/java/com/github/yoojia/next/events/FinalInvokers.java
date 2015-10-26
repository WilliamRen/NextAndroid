package com.github.yoojia.next.events;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
class FinalInvokers {

    private final Handler mMainThread = new Handler(Looper.getMainLooper());
    private final ExecutorService mThreads;

    FinalInvokers(ExecutorService threads) {
        mThreads = threads;
    }

    public void invokeInThreads(Callable<Void> task) {
        mThreads.submit(task);
    }

    public void invokeInMainThread(final Callable<Void> task) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                try {
                    task.call();
                } catch (Exception error) {
                    throw new IllegalStateException(error);
                }
            }
        });
    }
}
