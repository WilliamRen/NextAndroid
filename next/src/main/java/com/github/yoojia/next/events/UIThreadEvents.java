package com.github.yoojia.next.events;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class UIThreadEvents extends NextEvents{

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public UIThreadEvents(ExecutorService threads, String tag) {
        super(threads, tag);
    }

    public UIThreadEvents(int threads, String tag) {
        super(threads, tag);
    }

    @Override
    protected void trySubmitTask(final Callable<Void> task) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                try {
                    task.call();
                } catch (Exception error) {
                    // throw it to ui thread
                    throw new IllegalStateException(error);
                }
            }
        });
    }
}
