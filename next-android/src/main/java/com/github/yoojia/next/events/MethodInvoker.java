package com.github.yoojia.next.events;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class MethodInvoker extends Invokable<Method> {

    private final Object mHost;
    protected MethodInvoker(Object host, Meta[] events, Method invokable, boolean async) {
        super(events, invokable, async);
        mHost = host;
    }

    @Override
    public void invoke(Map<String, Object> params) throws Exception {
        // 将Map转换成Method的参数列表
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
