package com.github.yoojia.next.events;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class UIThreadEvents extends NextEvents{

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public UIThreadEvents(ExecutorService threads, String tag, Class<?> stopAtParentType) {
        super(threads, tag, stopAtParentType);
    }

    public UIThreadEvents(int threads, String tag, Class<?> stopAtParentType) {
        super(threads, tag, stopAtParentType);
    }

    public UIThreadEvents(String tag, Class<?> stopAtParentType) {
        super(tag, stopAtParentType);
    }

    @Override
    protected void trySubmitTask(Runnable task) {
        mHandler.post(task);
    }
}
