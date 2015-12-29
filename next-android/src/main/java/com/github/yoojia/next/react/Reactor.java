package com.github.yoojia.next.react;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 事件响应处理
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public class Reactor<T> {

    private final List<Subscription<T>> mSubscriptions = new CopyOnWriteArrayList<>();
    private final Map<Subscriber<T>, Subscription> mRefs = new ConcurrentHashMap<>();
    private final AtomicReference<Schedule> mSchedule;
    private final AtomicReference<OnTargetMissListener<T>> mOnTargetMissListener;

    public Reactor(Schedule subscribeOn) {
        mSchedule = new AtomicReference<>(subscribeOn);
        mOnTargetMissListener = new AtomicReference<>();
        mOnTargetMissListener.set(new OnTargetMissListener<T>() {
            @Override
            public void onTargetMiss(T input) {
                throw new IllegalStateException("Invoke target is MISSED, Input: " + input);
            }
        });
    }

    public synchronized Reactor<T> add(Subscription<T> newSub) {
        if (mSubscriptions.contains(newSub) || mRefs.containsKey(newSub.target)) {
            throw new IllegalStateException("Duplicate Subscription/Subscription.subscriber");
        }
        mSubscriptions.add(newSub);
        mRefs.put(newSub.target, newSub);
        return this;
    }

    public synchronized Reactor<T> remove(Subscriber<T> oldSub) {
        final Subscription s = mRefs.remove(oldSub);
        if (s != null) {
            mSubscriptions.remove(s);
        }
        return this;
    }

    public Reactor<T> emit(final T input) {
        final Schedule schedule = mSchedule.get();
        int hits = 0;
        for (final Subscription<T> sub : mSubscriptions) {
            if (sub.accept(input)) {
                hits += 1;
                try {
                    schedule.invoke(new CallableTask() {
                        @Override void onCall() throws Exception {
                            sub.target.onCall(input);
                        }
                    }, sub.scheduleFlag);
                } catch (Exception errorWhenCall) {
                    sub.target.onErrors(input, errorWhenCall);
                }
            }
        }
        final OnTargetMissListener<T> listener = mOnTargetMissListener.get();
        if (hits <= 0 && listener != null) {
            listener.onTargetMiss(input);
        }
        return this;
    }

    public Reactor<T> subscribeOn(Schedule schedule) {
        mSchedule.set(schedule);
        return this;
    }

    public Reactor<T> onEventListener(OnTargetMissListener<T> listener) {
        mOnTargetMissListener.set(listener);
        return this;
    }

    private static abstract class CallableTask implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            onCall();
            return null;
        }

        abstract void onCall() throws Exception;
    }

}
