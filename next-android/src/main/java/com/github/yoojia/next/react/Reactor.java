package com.github.yoojia.next.react;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public class Reactor<T> {

    private final List<Subscription<T>> mSubs = new CopyOnWriteArrayList<>();
    private final Map<Subscriber<T>, Subscription> mRefs = new ConcurrentHashMap<>();
    private final Schedule mEmitSchedule;
    private final AtomicReference<Schedule> mSubSchedule;

    public Reactor() {
        mEmitSchedule = Schedules.caller();
        mSubSchedule = new AtomicReference<>(Schedules.singleThread());
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
        final Callable<Void> task = newEmitWrapTask(input);
        try {
            mEmitSchedule.submit(task, Schedule.FLAG_ON_CALLER);
        } catch (Exception err) {
            throw new RuntimeException(err);
        }
        return this;
    }

    public void close(){
        mEmitSchedule.close();
        // Never null
        mSubSchedule.get().close();
    }

    public Reactor<T> subscribeOn(Schedule schedule) {
        if (schedule == null) {
            throw new NullPointerException();
        }
        mSubSchedule.set(schedule);
        return this;
    }

    private Callable<Void> newEmitWrapTask(final T input){
        return new Callable<Void>() {
            @Override public Void call() throws Exception {
                for (Subscription<T> callable : mSubs) {
                    if ( ! callable.filter(input)) {
                        continue;
                    }
                    final Callable<Void> finalRunTask = newSubscribeTask(callable, input);
                    final Schedule schedule = mSubSchedule.get();
                    schedule.submit(finalRunTask, callable.targetScheduleFlags);
                }
                return null;
            }
        };
    }

    private Callable<Void> newSubscribeTask(final Subscription<T> sub, final T input) {
        return new Callable<Void>() {
            @Override public Void call() throws Exception {
                try{
                    sub.target.onCall(input);
                }catch (Exception err) {
                    sub.target.onErrors(input, err);
                }
                return null;
            }
        };
    }

}
