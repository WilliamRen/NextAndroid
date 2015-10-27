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

    private final Set<Target> mTargetSet = new LinkedHashSet<>();
    private final AtomicInteger mTriggeredCount = new AtomicInteger(0);
    private final AtomicInteger mOverrideCount = new AtomicInteger(0);
    private final AtomicInteger mDeadEventsCount = new AtomicInteger(0);

    public void register(Subscriber subscriber, boolean async, Meta... events) {
        synchronized (mTargetSet) {
            mTargetSet.add(new Target(subscriber, async, mOverrideCount, events));
        }
    }

    public void unregister(Object host) {
        synchronized (mTargetSet) {
            for (Iterator<Target> it = mTargetSet.iterator(); it.hasNext();) {
                final Target target = it.next();
                if (target.subscriber.isSameHost(host)) {
                    it.remove();
                }
            }
        }
    }

    public List<Target.Trigger> emit(String event, Object value, boolean lenient) {
        final List<Target.Trigger> output = new ArrayList<>();
        synchronized (mTargetSet) {
            boolean accepted = false; // 事件是否被某一个目标接受
            for (Target target : mTargetSet) {
                if( ! target.isMatched(event, value)) {
                    continue;
                }
                accepted = true;
                final Target.Trigger trigger = target.emit(event, value);
                if (trigger != null) {
                    output.add(trigger);
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
        return output;
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
