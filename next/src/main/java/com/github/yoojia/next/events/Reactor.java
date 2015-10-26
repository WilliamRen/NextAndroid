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

    private final Set<Target> mTargetSet = new LinkedHashSet<>();
    private final AtomicInteger mTriggeredCount = new AtomicInteger(0);
    private final AtomicInteger mOverrideCount = new AtomicInteger(0);
    private final AtomicInteger mDeadEventsCount = new AtomicInteger(0);

    public void register(Subscriber subscriber, boolean async, Meta... events) {
        synchronized (mTargetSet) {
            mTargetSet.add(new Target(mOverrideCount, subscriber, async, events));
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

    public List<HotTarget> emit(String event, Object value, boolean lenient) {
        final List<HotTarget> output = new ArrayList<>();
        synchronized (mTargetSet) {
            boolean accepted = false; // 事件是否被某一个目标接受
            for (Target target : mTargetSet) {
                if( ! target.isMatched(event, value)) {
                    continue;
                }
                accepted = true;
                final HotTarget hotTarget = target.emit(event, value);
                if (hotTarget != null) {
                    output.add(hotTarget);
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

    private static class Target {

        final Subscriber subscriber;
        final boolean async;

        private final Map<String, Meta> mWraps;
        private final Map<String, Object> mEvents;
        private final AtomicInteger mOverrideCountRef;

        public Target(AtomicInteger overrideRef, Subscriber subscriber, boolean async, Meta... events) {
            mOverrideCountRef = overrideRef;
            mEvents = new HashMap<>(events.length);
            mWraps = new HashMap<>(events.length);
            this.async = async;
            this.subscriber = subscriber;
            for (Meta item : events) {
                mWraps.put(item.event, item);
                mEvents.put(item.event, NULL_VALUE);
            }
        }

        public HotTarget emit(String event, Object value) {
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

        private HotTarget getTriggered(){
            final HotTarget copy = new HotTarget(subscriber, mEvents, async);
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
            final Target target = (Target) o;
            if (!mEvents.equals(target.mEvents)) return false;
            return subscriber.equals(target.subscriber);
        }

        @Override
        public int hashCode() {
            final int result = mEvents.hashCode();
            return 31 * result + subscriber.hashCode();
        }

        private static class NullValue {
            @Override public String toString() {
                return "<REACTOR-NULL-VALUE>";
            }
        }

        private static NullValue NULL_VALUE = new NullValue();

    }

    public static class HotTarget {

        private final Map<String, Object> mEvents = new HashMap<>();
        private final Subscriber mSubscriber;

        public final boolean async;

        private HotTarget(Subscriber subscriber, Map<String, Object> events, boolean async) {
            this.mSubscriber = subscriber;
            this.async = async;
            this.mEvents.putAll(events);
        }

        public void invoke() throws Exception {
            mSubscriber.notify(mEvents);
        }

    }

}
