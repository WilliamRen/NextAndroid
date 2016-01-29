package com.github.yoojia.next.events.supports;

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

    private final List<Descriptor<T>> mDescriptors = new CopyOnWriteArrayList<>();
    private final Map<Subscriber<T>, Descriptor> mRefs = new ConcurrentHashMap<>();

    /**
     * 事件调试器
     */
    private final AtomicReference<Schedule> mSchedule;

    /**
     * 事件没有订阅者时的处理接口
     */
    private final AtomicReference<OnEventsListener<T>> mOnEventsListener;

    public Reactor(Schedule subscribeOn) {
        mSchedule = new AtomicReference<>(subscribeOn);
        mOnEventsListener = new AtomicReference<>();
        mOnEventsListener.set(new OnEventsListener<T>() {
            @Override
            public void onWithoutSubscriber(T input) {
                throw new IllegalStateException("Invoke target is MISSED, Input: " + input);
            }
        });
    }

    public synchronized Reactor<T> add(Descriptor<T> newDes) {
        if (mDescriptors.contains(newDes) || mRefs.containsKey(newDes.subscriber)) {
            throw new IllegalStateException("Duplicate Subscription/Subscription.subscriber");
        }
        mDescriptors.add(newDes);
        mRefs.put(newDes.subscriber, newDes);
        return this;
    }

    public synchronized Reactor<T> remove(Subscriber<T> oldSub) {
        final Descriptor s = mRefs.remove(oldSub);
        if (s != null) {
            mDescriptors.remove(s);
        }
        return this;
    }

    public Reactor<T> emit(final T input) {
        final Schedule schedule = mSchedule.get();
        int hits = 0;
        for (final Descriptor<T> sub : mDescriptors) {
            if (sub.accept(input)) {
                hits += 1;
                try {
                    schedule.invoke(new CallableTask() {
                        @Override void onCall() throws Exception {
                            sub.subscriber.onCall(input);
                        }
                    }, sub.scheduleFlag);
                } catch (Exception errorWhenCall) {
                    sub.subscriber.onErrors(input, errorWhenCall);
                }
            }
        }
        final OnEventsListener<T> listener = mOnEventsListener.get();
        if (hits <= 0 && listener != null) {
            listener.onWithoutSubscriber(input);
        }
        return this;
    }

    public Reactor<T> subscribeOn(Schedule schedule) {
        mSchedule.set(schedule);
        return this;
    }

    public Reactor<T> onEventsListener(OnEventsListener<T> listener) {
        mOnEventsListener.set(listener);
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
