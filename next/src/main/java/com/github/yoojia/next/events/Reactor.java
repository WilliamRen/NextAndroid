package com.github.yoojia.next.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class Reactor {

    private final Lock mLock = new ReentrantLock();
    private final Set<Wrap> mWrapSet = new LinkedHashSet<>();
    private final AtomicInteger mTriggeredCount = new AtomicInteger(0);
    private final AtomicInteger mOverrideCount = new AtomicInteger(0);
    private final AtomicInteger mDeadEventsCount = new AtomicInteger(0);

    public synchronized void register(Target target, EventWrap... events) {
        mWrapSet.add(new Wrap(mOverrideCount, target, events));
    }

    public synchronized void unregister(Object host) {
        if (host == null) {
            throw new NullPointerException("Host must not be null !");
        }
        for (Iterator<Wrap> it = mWrapSet.iterator(); it.hasNext();) {
            final Wrap wrap = it.next();
            if (wrap.mTarget.isSameHost(host)) {
                it.remove();
            }
        }
    }

    public List<Trigger> emit(String event, Object value) {
        final List<Trigger> output = new ArrayList<>();
        mLock.lock();
        boolean triggered = false;
        for (Wrap wrap : mWrapSet) {
            if( ! wrap.isMatched(event, value)) {
                continue;
            }
            triggered = true;
            final Trigger trigger = wrap.emit(event, value);
            if (trigger != null) {
                mTriggeredCount.addAndGet(1);
                output.add(trigger);
            }
        }
        if ( ! triggered) {
            mDeadEventsCount.addAndGet(1);
        }
        mLock.unlock();
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

    private static class Wrap {

        private final Map<String, EventWrap> mEventWraps;
        private final Map<String, Object> mEvents;
        private final Target mTarget;
        private final AtomicInteger mOverrideCountRef;

        public Wrap(AtomicInteger overrideCountRef, Target target, EventWrap... events) {
            mOverrideCountRef = overrideCountRef;
            mEvents = new HashMap<>(events.length);
            mEventWraps = new HashMap<>(events.length);
            mTarget = target;
            for (EventWrap item : events) {
                mEventWraps.put(item.event, item);
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
            final EventWrap wrap = mEventWraps.get(event);
            return wrap.type.equals(value.getClass());
        }

        private boolean isTriggered(){
            return ! mEvents.containsValue(NULL_VALUE);
        }

        private Trigger getTriggered(){
            final Trigger copy = new Trigger(mTarget, mEvents);
            // reset after copy
            for (EventWrap item : mEventWraps.values()) {
                mEvents.put(item.event, NULL_VALUE);
            }
            return copy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Wrap wrap = (Wrap) o;
            if (!mEvents.equals(wrap.mEvents)) return false;
            return mTarget.equals(wrap.mTarget);
        }

        @Override
        public int hashCode() {
            final int result = mEvents.hashCode();
            return 31 * result + mTarget.hashCode();
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
        private final Target target;

        private Trigger(Target target, Map<String, Object> events) {
            this.target = target;
            this.events.putAll(events);
        }

        public void invoke() throws Exception {
            target.invoke(events);
        }

    }

}
