package com.github.yoojia.next.events;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class MethodSubscriber implements Subscriber {

    public final boolean mAsync;
    private final Object mHost;
    private final Method mMethod;
    private final String[] mOrderedEvents;

    MethodSubscriber(String[] orderedEvents, boolean async, Object host, Method method) {
        mOrderedEvents = orderedEvents;
        mAsync = async;
        mHost = host;
        mMethod = method;
    }

    @Override
    public void notify(Map<String, Object> events) throws Exception {
        final Object[] params = new Object[mOrderedEvents.length];
        for (int i = 0; i < mOrderedEvents.length; i++) {
            params[i] = events.get(mOrderedEvents[i]);
        }
        final boolean origin = mMethod.isAccessible();
        mMethod.setAccessible(true);
        mMethod.invoke(mHost, params);
        if (!origin) {
            mMethod.setAccessible(false);
        }
    }

    @Override
    public boolean isSameHost(Object host) {
        return mHost == host;
    }

}
