package com.github.yoojia.next.events;

import android.util.Log;

import com.github.yoojia.next.lang.Filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
class AnnotatedRegister {

    private final Object mHost;
    private final InvokableAccess<MethodInvokable> mListener;

    public AnnotatedRegister(Object host, InvokableAccess<MethodInvokable> listener) {
        mHost = host;
        mListener = listener;
    }

    public void batch(List<Method> annotatedMethods, Filter<Method> customFilter) {
        for (Method method : annotatedMethods){
            // Filter
            if (customFilter != null && !customFilter.accept(method)) {
                continue;
            }
            // BASIC CHECK: Check if return type is Void
            if (! Void.TYPE.equals(method.getReturnType())) {
                Log.w("Register", "Found @Subscribe method return a non-void type. We recommend a void type.");
            }
            // BASIC CHECK: Arguments count
            if (method.getParameterTypes().length == 0) {
                throw new IllegalArgumentException("@Subscribe methods must require at less one arguments.");
            }
            final boolean origin = method.isAccessible();
            method.setAccessible(true);
            final Subscribe conf = method.getAnnotation(Subscribe.class);
            if (!origin) {
                method.setAccessible(false);
            }
            mListener.access(new MethodInvokable(mHost, makeMeta(method), method, conf.async()));
        }
    }

    private Meta[] makeMeta(Method method){
        final Class<?>[] types = method.getParameterTypes();
        final Annotation[][] as = method.getParameterAnnotations();
        final Meta[] events = new Meta[types.length];
        for (int i = 0; i < types.length; i++) {
            final Annotation[] annotations = as[i];
            if (annotations.length == 0) {
                throw new IllegalArgumentException("All params must has a @Event annotation in method: " + method);
            }
            final Event event = (Event) annotations[0];
            events[i] = new Meta(event.value(), types[i]);
        }
        return events;
    }

}
