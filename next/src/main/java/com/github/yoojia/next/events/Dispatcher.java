package com.github.yoojia.next.events;

import android.os.Handler;
import android.os.Looper;

import com.github.yoojia.next.lang.QuantumObject;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
class Dispatcher {

    private final Handler mMainThread = new Handler(Looper.getMainLooper());
    private final ExecutorService mThreads;
    private final QuantumObject<OnErrorsListener> mOnErrorsListener;

    Dispatcher(ExecutorService threads, QuantumObject<OnErrorsListener> onErrorsListener) {
        mThreads = threads;
        mOnErrorsListener = onErrorsListener;
    }

    public void dispatch(List<Reactor.Trigger> triggers){
        for (final Reactor.Trigger trigger : triggers){
            final Callable<Void> task = new Callable<Void>() {
                @Override public Void call() throws Exception {
                    try {
                        trigger.invoke();
                    } catch (Exception error) {
                        if (mOnErrorsListener.watch()) {
                            mOnErrorsListener.get().onErrors(error);
                        }else{
                            throw error;
                        }
                    }
                    return null;
                }
            };
            if (trigger.async) {
                invokeInMainThread(task);
            }else{
                invokeInThreads(task);
            }
        }
    }

    private void invokeInThreads(Callable<Void> task) {
        mThreads.submit(task);
    }

    private void invokeInMainThread(final Callable<Void> task) {
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
