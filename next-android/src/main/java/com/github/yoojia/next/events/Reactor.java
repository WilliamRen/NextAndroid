package com.github.yoojia.next.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class Reactor {

    private final Set<FuelTarget> mTargetCached = new CopyOnWriteArraySet<>();

    public void add(Subscriber subscriber) {
        mTargetCached.add(new FuelTarget(subscriber));
    }

    public void removeByHost(Object host) {
        // CopyOnWriteArraySet not support iterator.remove()
        final List<FuelTarget> removes = new ArrayList<>();
        for (FuelTarget target : mTargetCached) {
            if (target.subscriber.isSameHost(host)) {
                removes.add(target);
            }
        }
        mTargetCached.removeAll(removes);
    }

    public List<FuelTarget.Target> emit(String event, Object value, boolean lenient) {
        final List<FuelTarget.Target> triggers = new ArrayList<>();
        boolean accepted = false;
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
