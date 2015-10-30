package com.github.yoojia.next.events;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
class Target {

    public final Invokable invokable;

    private final HashMap<String, Meta> mMeta;
    private final HashMap<String, Object> mValues;

    Target(Invokable invokable) {
        this.invokable = invokable;
        mMeta = new HashMap<>(invokable.events.length);
        mValues = new HashMap<>(invokable.events.length);
        // init
        for (Meta meta : invokable.events) {
            mMeta.put(meta.event, meta);
            mValues.put(meta.event, NULL_VALUE);
        }
    }

    public Trigger emit(String event, Object value) {
        if (NULL_VALUE != mValues.get(event)) {
            Log.w("FuelTarget", "- Override event: " + event);
        }
        mValues.put(event, value);
        if (mValues.containsValue(NULL_VALUE)) {
            return null;
        }else{
            final Trigger trigger = new Trigger(invokable, mValues);
            // reset value
            for (Meta item : mMeta.values()) {
                mValues.put(item.event, NULL_VALUE);
            }
            return trigger;
        }
    }

    public boolean accept(String event, Object value) {
        if (!mValues.containsKey(event)) {
            return false;
        }
        final Meta meta = mMeta.get(event);
        return meta.type.equals(value.getClass());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Target target = (Target) o;
        if (!mValues.equals(target.mValues)) return false;
        return invokable.equals(target.invokable);
    }

    @Override
    public int hashCode() {
        final int result = mValues.hashCode();
        return 31 * result + invokable.hashCode();
    }

    public static class Trigger {

        private final Map<String, Object> mEvents = new HashMap<>();
        private final Invokable mInvokable;

        public final Set<String> eventNames;

        private Trigger(Invokable invokable, Map<String, Object> events) {
            this.mInvokable = invokable;
            this.mEvents.putAll(events);
            this.eventNames = events.keySet();
        }

        public void invoke() throws Exception {
            mInvokable.invoke(mEvents);
        }

        public boolean runAsync() {
            return mInvokable.runAsync;
        }

    }

    private static class NullValue {
        @Override public String toString() {
            return "<REACTOR-NULL-VALUE>";
        }
    }

    private static NullValue NULL_VALUE = new NullValue();
}
