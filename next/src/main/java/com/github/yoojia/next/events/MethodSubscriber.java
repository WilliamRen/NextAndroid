package com.github.yoojia.next.events;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
final class MethodSubscriber extends Subscriber<Method> {

    protected MethodSubscriber(Object host, Meta[] events, Method invokable, boolean async) {
        super(host, events, invokable, async);
    }

    @Override
    public void invoke(Map<String, Object> params) throws Exception {
        final Object[] args = new Object[events.length];
        for (int i = 0; i < events.length; i++) {
            args[i] = params.get(events[i].event);
        }
        final boolean origin = invokable.isAccessible();
        invokable.setAccessible(true);
        invokable.invoke(host, args);
        if (!origin) {
            invokable.setAccessible(false);
        }
    }

}
