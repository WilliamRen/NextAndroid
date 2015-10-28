package com.github.yoojia.next.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class Reactor {

    private final Set<FuelTarget> mTargetCached = new LinkedHashSet<>();

    public void add(Subscriber subscriber) {
        mTargetCached.add(new FuelTarget(subscriber));
    }

    public void removeByHost(Object host) {
        for (Iterator<FuelTarget> it = mTargetCached.iterator(); it.hasNext();) {
            if (it.next().subscriber.isSameHost(host)) {
                it.remove();
            }
        }
    }

    public List<FuelTarget.Target> emit(String event, Object value, boolean lenient) {
        final List<FuelTarget.Target> triggers = new ArrayList<>();
        boolean accepted = false; // 事件是否被某一个目标接受
        for (FuelTarget target : mTargetCached) {
            if( ! target.accept(event, value)) {
                continue;
            }
            accepted = true;
            final FuelTarget.Target trigger = target.emit(event, value);
            if (trigger != null) {
                triggers.add(trigger);
            }
        }
        if (!lenient && !accepted) {
            throw new IllegalStateException("Event without a subscriber: " + event);
        }
        return triggers;
    }

}
