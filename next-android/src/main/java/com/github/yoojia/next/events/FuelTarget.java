package com.github.yoojia.next.events;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
class FuelTarget {

    public final Invokable invokable;

    private final HashMap<String, Meta> mMeta;
    private final HashMap<String, Object> mValues;

    FuelTarget(Invokable invokable) {
        this.invokable = invokable;
        mMeta = new HashMap<>(invokable.events.length);
        mValues = new HashMap<>(invokable.events.length);
        // init
        for (Meta meta : invokable.events) {
            mMeta.put(meta.event, meta);
            mValues.put(meta.event, NULL_VALUE);
        }
    }

    public Target emit(String event, Object value) {
        if (NULL_VALUE != mValues.get(event)) {
            Log.w("FuelTarget", "- Override event: " + event);
        }
        mValues.put(event, value);
        if (mValues.containsValue(NULL_VALUE)) {
            return null;
        }else{
            final Target target = new Target(invokable, mValues, invokable.async);
            // reset value
            for (Meta item : mMeta.values()) {
                mValues.put(item.event, NULL_VALUE);
            }
            return target;
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
        final FuelTarget target = (FuelTarget) o;
        if (!mValues.equals(target.mValues)) return false;
        return invokable.equals(target.invokable);
    }

    @Override
    public int hashCode() {
        final int result = mValues.hashCode();
        return 31 * result + invokable.hashCode();
    }

    public static class Target {

        private final Map<String, Object> mEvents = new HashMap<>();
        private final Invokable mInvokable;

        public final Set<String> eventNames;
        public final boolean runAsync;

        private Target(Invokable invokable, Map<String, Object> events, boolean runAsync) {
            this.mInvokable = invokable;
            this.runAsync = runAsync;
            this.mEvents.putAll(events);
            this.eventNames = events.keySet();
        }

        public void invoke() throws Exception {
            mInvokable.invoke(mEvents);
        }

    }

    private static class NullValue {
        @Override public String toString() {
            return "<REACTOR-NULL-VALUE>";
        }
    }

    private static NullValue NULL_VALUE = new NullValue();
}
