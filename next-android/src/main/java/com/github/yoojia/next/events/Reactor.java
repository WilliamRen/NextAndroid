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

    private final Set<Target> mTargetCached = new CopyOnWriteArraySet<>();

    public void add(Invokable invokable) {
        mTargetCached.add(new Target(invokable));
    }

    public void remove(Object host) {
        // CopyOnWriteArraySet not support iterator.remove()
        final List<Target> removes = new ArrayList<>();
        for (Target target : mTargetCached) {
            if (target.invokable.isRemovable(host)) {
                removes.add(target);
            }
        }
        mTargetCached.removeAll(removes);
    }

    public List<Target.Trigger> emit(String event, Object value, boolean lenient) {
        final List<Target.Trigger> triggers = new ArrayList<>();
        boolean accepted = false;
        for (Target target : mTargetCached) {
            if( ! target.accept(event, value)) {
                continue;
            }
            accepted = true;
            final Target.Trigger trigger = target.emit(event, value);
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
