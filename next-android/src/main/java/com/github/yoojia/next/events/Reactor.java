package com.github.yoojia.next.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class Reactor {

    private final List<Target> mTargets = new ArrayList<>();

    public void add(Invokable invokable) {
        mTargets.add(new Target(invokable));
    }

    public void remove(Object host) {
        synchronized (mTargets) {
            for (Iterator<Target> iterator = mTargets.iterator(); iterator.hasNext();) {
                if (iterator.next().invokable.isRemovable(host)) {
                    iterator.remove();
                }
            }
        }
    }

    public List<Target.Trigger> emit(String event, Object value, boolean lenient) {
        final List<Target.Trigger> triggers = new ArrayList<>();
        boolean accepted = false;
        for (Target target : mTargets) {
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
            throw new IllegalStateException("No subscribers handle the event: " + event);
        }
        return triggers;
    }

}
