package com.github.yoojia.next.events;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
class Target {

    public final Subscriber subscriber;
    public final boolean async;

    private final Map<String, Meta> mWraps;
    private final Map<String, Object> mEvents;
    private final AtomicInteger mOverrideCountRef;

    Target(Subscriber subscriber, boolean async, AtomicInteger counter, Meta... events) {
        this.subscriber = subscriber;
        this.async = async;
        mOverrideCountRef = counter;
        mEvents = new HashMap<>(events.length);
        mWraps = new HashMap<>(events.length);
        // init
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
        final Trigger copy = new Trigger(subscriber, mEvents, async);
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

    public static class Trigger {

        private final Map<String, Object> mEvents = new HashMap<>();
        private final Subscriber mSubscriber;

        public final boolean async;

        private Trigger(Subscriber subscriber, Map<String, Object> events, boolean async) {
            this.mSubscriber = subscriber;
            this.async = async;
            this.mEvents.putAll(events);
        }

        public void invoke() throws Exception {
            mSubscriber.invoke(mEvents);
        }

    }

    private static class NullValue {
        @Override public String toString() {
            return "<REACTOR-NULL-VALUE>";
        }
    }

    private static NullValue NULL_VALUE = new NullValue();
}
