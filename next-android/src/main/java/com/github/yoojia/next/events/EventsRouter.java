package com.github.yoojia.next.events;

import android.util.Log;

import com.github.yoojia.next.lang.QuantumObject;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
class EventsRouter {

    private static final String TAG = EventsRouter.class.getSimpleName();

    private final Schedulers mThreads;
    private final QuantumObject<OnErrorsListener> mOnErrorsListener;

    EventsRouter(Schedulers threads, QuantumObject<OnErrorsListener> onErrorsListener) {
        mThreads = threads;
        mOnErrorsListener = onErrorsListener;
    }

    public void dispatch(List<FuelTarget.Target> targets){
        for (final FuelTarget.Target target : targets){
            final Callable<Void> finalTask = new Callable<Void>() {
                @Override public Void call() throws Exception {
                    if (EventsFlags.PROCESSING) {
                        Log.d(TAG, "- Target run on thread.id= " + Thread.currentThread().getId());
                    }
                    try {
                        target.invoke();
                    } catch (Exception error) {
                        if (mOnErrorsListener.has()) {
                            mOnErrorsListener.get().onErrors(target.eventNames, error);
                        }else{
                            throw error;
                        }
                    }
                    return null;
                }
            };
            try{
                mThreads.submit(finalTask, target.runAsync());
            }catch (Exception error) {
                if (mOnErrorsListener.has()) {
                    mOnErrorsListener.get().onErrors(target.eventNames, error);
                }else{
                    throw new IllegalStateException(error);
                }
            }
        }
    }

    public void shutdown(){
        mThreads.close();
    }

}
