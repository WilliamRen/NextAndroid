package com.github.yoojia.next.events;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class MethodInvokable extends Invokable<Method> {

    private final Object mHost;
    protected MethodInvokable(Object host, Meta[] events, Method invokable, boolean async) {
        super(events, invokable, async);
        mHost = host;
    }

    @Override
    public void invoke(Map<String, Object> params) throws Exception {
        final Object[] args = new Object[events.length];
        for (int i = 0; i < events.length; i++) {
            args[i] = params.get(events[i].event);
        }
        final boolean origin = invokable.isAccessible();
        invokable.setAccessible(true);
        invokable.invoke(mHost, args);
        if (!origin) {
            invokable.setAccessible(false);
        }
    }

    @Override
    public boolean isRemovable(Object other) {
        return mHost == other;
    }

}
