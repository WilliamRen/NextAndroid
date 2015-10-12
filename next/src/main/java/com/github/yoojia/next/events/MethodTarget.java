package com.github.yoojia.next.events;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class MethodTarget implements Target{

    public final boolean mAsync;
    private final Object mHost;
    private final Method mMethod;
    private final String[] mOrderedEvents;

    MethodTarget(String[] orderedEvents, boolean async, Object host, Method method) {
        mOrderedEvents = orderedEvents;
        mAsync = async;
        mHost = host;
        mMethod = method;
    }

    @Override
    public void invoke(Map<String, Object> events) throws Exception{
        final Object[] params = new Object[mOrderedEvents.length];
        for (int i = 0; i < mOrderedEvents.length; i++) {
            params[i] = events.get(mOrderedEvents[i]);
        }
        mMethod.setAccessible(true);
        mMethod.invoke(mHost, params);
    }

    @Override
    public boolean isSameHost(Object host) {
        return mHost == host;
    }

}
