package com.github.yoojia.next.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class Reactor {

    private final Set<FuelTarget> mTargetCached = new LinkedHashSet<>();
    private final AtomicInteger mTriggeredCount = new AtomicInteger(0);
    private final AtomicInteger mOverrideCount = new AtomicInteger(0);
    private final AtomicInteger mDeadEventsCount = new AtomicInteger(0);

    public void add(Subscriber subscriber) {
        synchronized (mTargetCached) {
            mTargetCached.add(new FuelTarget(subscriber, mOverrideCount));
        }
    }

    public void removeByHost(Object host) {
        synchronized (mTargetCached) {
            for (Iterator<FuelTarget> it = mTargetCached.iterator(); it.hasNext();) {
                final FuelTarget target = it.next();
                if (target.subscriber.isSameHost(host)) {
                    it.remove();
                }
            }
        }
    }

    public List<FuelTarget.Target> emit(String event, Object value, boolean lenient) {
        final List<FuelTarget.Target> triggers = new ArrayList<>();
        synchronized (mTargetCached) {
            boolean accepted = false; // 事件是否被某一个目标接受
            for (FuelTarget target : mTargetCached) {
                if( ! target.accept(event, value)) {
                    continue;
                }
                accepted = true;
                final FuelTarget.Target trigger = target.emit(event, value);
                if (trigger != null) {
                    triggers.add(trigger);
                    mTriggeredCount.addAndGet(1);
                }
            }
            if (!lenient && !accepted) {
                throw new IllegalStateException("Event without a subscriber: " + event);
            }
            if ( ! accepted) {
                mDeadEventsCount.addAndGet(1);
            }
        }
        return triggers;
    }

    public int getTriggeredCount(){
        return mTriggeredCount.get();
    }

    public int getOverrideCount() {
        return mOverrideCount.get();
    }

    public int getDeadEventsCount(){
        return mDeadEventsCount.get();
    }

}
