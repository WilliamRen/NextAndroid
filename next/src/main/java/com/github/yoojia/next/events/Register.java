package com.github.yoojia.next.events;

import android.util.Log;

import com.github.yoojia.next.lang.Filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
class Register {

    private final String mTag;
    private final Reactor mReactor;
    private final Object mTargetHost;

    public Register(String tag, Reactor reactor, Object targetHost) {
        mTag = tag;
        mReactor = reactor;
        mTargetHost = targetHost;
    }

    public void batch(List<Method> annotatedMethods, Filter<Method> filter) {
        for (Iterator<Method> iterator = annotatedMethods.iterator(); iterator.hasNext();){
            final Method method = iterator.next();
            // BASIC CHECK: Check if return type is Void
            if (! Void.TYPE.equals(method.getReturnType())) {
                Log.w(mTag, "Found @Subscribe method return a non-void type. We recommend a void type.");
            }
            // BASIC CHECK: Arguments count
            if (method.getParameterTypes().length == 0) {
                throw new IllegalArgumentException("@Subscribe methods must require at less one arguments.");
            }
            // Filter
            if (filter != null && !filter.accept(method)) {
                iterator.remove();
                continue;
            }
            final Meta[] events = makeMeta(method);
            final String[] orderedEvents = new String[events.length];
            for (int i = 0; i < events.length; i++) {
                orderedEvents[i] = events[i].event;
            }
            final boolean origin = method.isAccessible();
            method.setAccessible(true);
            final Subscribe conf = method.getAnnotation(Subscribe.class);
            if (!origin) {
                method.setAccessible(false);
            }
            final MethodSubscriber target = new MethodSubscriber(orderedEvents, mTargetHost, method);
            mReactor.register(target, conf.async(), events);
        }
    }

    private Meta[] makeMeta(Method method){
        final Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) {
            throw new IllegalArgumentException("Require ONE or MORE params in method: " + method);
        }
        final Annotation[][] anns = method.getParameterAnnotations();
        final Meta[] events = new Meta[types.length];
        for (int i = 0; i < types.length; i++) {
            final Annotation[] annotations = anns[i];
            if (annotations.length == 0) {
                throw new IllegalArgumentException("All params must has a @Event annotation in method: " + method);
            }
            final Event event = (Event) annotations[0];
            events[i] = new Meta(event.value(), types[i]);
        }
        return events;
    }
}
