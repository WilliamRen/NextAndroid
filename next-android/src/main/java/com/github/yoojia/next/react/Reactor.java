package com.github.yoojia.next.react;

import com.github.yoojia.next.lang.ObjectWrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件响应处理
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public class Reactor<T> {

    private final List<Subscription<T>> mSubs = new CopyOnWriteArrayList<>();
    private final Map<Subscriber<T>, Subscription> mRefs = new ConcurrentHashMap<>();
    private final ObjectWrap<Schedule> mSubSchedule;

    public Reactor(Schedule subscribeOn) {
        mSubSchedule = new ObjectWrap<>(subscribeOn);
    }

    public synchronized Reactor<T> add(Subscription<T> newSub) {
        if (mSubs.contains(newSub) || mRefs.containsKey(newSub.target)) {
            throw new IllegalArgumentException("Duplicate Subscription/Subscription.subscriber");
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

    public Reactor<T> emit(T input) {
        try {
            emitInput(input);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
        return this;
    }

    public Reactor<T> subscribeOn(Schedule schedule) {
        if (schedule == null) {
            throw new NullPointerException();
        }
        mSubSchedule.set(schedule);
        return this;
    }

    private void emitInput(final T input) throws Exception {
        final Schedule schedule = mSubSchedule.get();
        for (final Subscription<T> callable : mSubs) {
            if ( ! callable.filter(input)) {
                continue;
            }
            schedule.submit(new Callable<Void>() {
                @Override public Void call() throws Exception {
                    try{
                        callable.target.onCall(input);
                    }catch (Exception err) {
                        callable.target.onErrors(input, err);
                    }
                    return null;
                }
            }, callable.targetScheduleFlags);
        }
    }

}
