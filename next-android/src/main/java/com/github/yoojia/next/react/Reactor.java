package com.github.yoojia.next.react;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.yoojia.next.lang.Preconditions.notNull;

/**
 * 事件响应处理
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public class Reactor<T> {

    private final List<Subscription<T>> mSubs = new CopyOnWriteArrayList<>();
    private final Map<Subscriber<T>, Subscription> mRefs = new ConcurrentHashMap<>();
    private final AtomicReference<Schedule> mScheduleWrap;
    private final AtomicReference<OnEventListener<T>> mOnEventListenerWrap = new AtomicReference<>();

    public Reactor(Schedule subscribeOn) {
        mScheduleWrap = new AtomicReference<>(subscribeOn);
    }

    public synchronized Reactor<T> add(Subscription<T> newSub) {
        if (mSubs.contains(newSub) || mRefs.containsKey(newSub.target)) {
            throw new IllegalStateException("Duplicate Subscription/Subscription.subscriber");
        }
        mSubs.add(newSub);
        mRefs.put(newSub.target, newSub);
        return this;
    }

    public synchronized Reactor<T> remove(Subscriber<T> oldSub) {
        final Subscription sn = mRefs.remove(oldSub);
        if (sn != null) {
            mSubs.remove(sn);
        }
        return this;
    }

    public Reactor<T> emit(final T input) {
        final Schedule schedule = mScheduleWrap.get();
        int hits = 0;
        for (final Subscription<T> callable : mSubs) {
            // filter at per emit action:
            if (callable.accept(input)) {
                hits += 1;
                try {
                    schedule.invoke(new Callable<Void>() {
                        @Override public Void call() throws Exception {
                            callable.target.onCall(input);
                            return null;
                        }
                    }, callable.targetScheduleFlags);
                } catch (Exception errorWhenCall) {
                    callable.target.onErrors(input, errorWhenCall);
                }
            }
        }
        final OnEventListener<T> listener = mOnEventListenerWrap.get();
        if (hits <= 0 && listener != null) {
            listener.onEventMiss(input);
        }
        return this;
    }

    public Reactor<T> subscribeOn(Schedule schedule) {
        mScheduleWrap.set(schedule);
        return this;
    }

    public Reactor<T> onEventListener(OnEventListener<T> listener) {
        mOnEventListenerWrap.set(listener);
        return this;
    }

}
