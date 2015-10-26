package com.github.yoojia.next.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class Reactor {

    private final Set<WrapTarget> mTargetSet = new LinkedHashSet<>();
    private final AtomicInteger mTriggeredCount = new AtomicInteger(0);
    private final AtomicInteger mOverrideCount = new AtomicInteger(0);
    private final AtomicInteger mDeadEventsCount = new AtomicInteger(0);

    public void register(Subscriber subscriber, Meta... events) {
        synchronized (mTargetSet) {
            mTargetSet.add(new WrapTarget(mOverrideCount, subscriber, events));
        }
    }

    public void unregister(Object host) {
        synchronized (mTargetSet) {
            for (Iterator<WrapTarget> it = mTargetSet.iterator(); it.hasNext();) {
                final WrapTarget wrapTarget = it.next();
                if (wrapTarget.mSubscriber.isSameHost(host)) {
                    it.remove();
                }
            }
        }
    }

    public List<Trigger> emit(String event, Object value, boolean lenient) {
        final List<Trigger> output = new ArrayList<>();
        synchronized (mTargetSet) {
            boolean accepted = false; // 事件是否被某一个目标接受
            for (WrapTarget target : mTargetSet) {
                if( ! target.isMatched(event, value)) {
                    continue;
                }
                accepted = true;
                final Trigger trigger = target.emit(event, value);
                if (trigger != null) {
                    output.add(trigger);
                    mTriggeredCount.addAndGet(1);
                }
            }
            if (!lenient && !accepted) {
                throw new IllegalStateException("Event without a mSubscriber: " + event);
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

    private static class WrapTarget {

        final Subscriber mSubscriber;

        private final Map<String, Meta> mWraps;
        private final Map<String, Object> mEvents;
        private final AtomicInteger mOverrideCountRef;

        public WrapTarget(AtomicInteger overrideCountRef, Subscriber subscriber, Meta... events) {
            mOverrideCountRef = overrideCountRef;
            mEvents = new HashMap<>(events.length);
            mWraps = new HashMap<>(events.length);
            this.mSubscriber = subscriber;
            for (Meta item : events) {
                mWraps.put(item.event, item);
                mEvents.put(item.event, NULL_VALUE);
            }
        }

        public Trigger emit(String event, Object value) {
            if (NULL_VALUE != mEvents.get(event)) {
                mOverrideCountRef.addAndGet(1);
            }
            mEvents.put(event, value);
            if (! isTriggered()) {
                return null;
            }else{
                return getTriggered();
            }
        }

        public boolean isMatched(String event, Object value) {
            if (!mEvents.containsKey(event)) {
                return false;
            }
            final Meta wrap = mWraps.get(event);
            return wrap.type.equals(value.getClass());
        }

        private boolean isTriggered(){
            return ! mEvents.containsValue(NULL_VALUE);
        }

        private Trigger getTriggered(){
            final Trigger copy = new Trigger(mSubscriber, mEvents);
            // reset after copy
            for (Meta item : mWraps.values()) {
                mEvents.put(item.event, NULL_VALUE);
            }
            return copy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final WrapTarget wrapTarget = (WrapTarget) o;
            if (!mEvents.equals(wrapTarget.mEvents)) return false;
            return mSubscriber.equals(wrapTarget.mSubscriber);
        }

        @Override
        public int hashCode() {
            final int result = mEvents.hashCode();
            return 31 * result + mSubscriber.hashCode();
        }

        private static class NullValue {
            @Override public String toString() {
                return "<REACTOR-NULL-VALUE>";
            }
        }

        private static NullValue NULL_VALUE = new NullValue();

    }

    public static class Trigger {

        private final Map<String, Object> events = new HashMap<>();
        private final Subscriber mSubscriber;

        private Trigger(Subscriber subscriber, Map<String, Object> events) {
            this.mSubscriber = subscriber;
            this.events.putAll(events);
        }

        public void invoke() throws Exception {
            mSubscriber.notify(events);
        }

    }

}
