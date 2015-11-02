package com.github.yoojia.next.events;

import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
class EventsRouter {

    private static final String TAG = EventsRouter.class.getSimpleName();

    private final Schedulers mSchedulers;

    EventsRouter(Schedulers schedulers) {
        mSchedulers = schedulers;
    }

    public void dispatch(List<Target.Trigger> triggers) throws Exception {
        for (final Target.Trigger trigger : triggers){
            final Callable<Void> finalTask = new Callable<Void>() {
                @Override public Void call() throws Exception {
                    if (EventsFlags.PROCESSING) {
                        Log.d(TAG, "- Target run on thread.id= " + Thread.currentThread().getId());
                    }
                    try {
                        trigger.invoke();
                    } catch (Exception exception) {
                        if (EventsFlags.PROCESSING) {
                            Log.d(TAG, "- Target invoke throws exceptions", exception);
                        }
                    }
                    return null;
                }
            };
            mSchedulers.submit(finalTask, trigger.runAsync());
        }
    }

    public void close(){
        mSchedulers.close();
    }

}
