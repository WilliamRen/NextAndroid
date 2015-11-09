package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Reactor;
import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Subscriber;
import com.github.yoojia.next.react.Subscription;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
public class NextEvents<T> {

    private final Reactor<Event<T>> mReactor = new Reactor<>();
    private final Map<Object, Subscriber<Event<T>>> mRefs = new ConcurrentHashMap<>();

    public NextEvents register(final Object target) {
        // Find @Subscribe methods
        final List<Method> annotatedMethods = new MethodFinder(target).find(new MethodFinder.Filter(){

            @Override
            public boolean acceptType(Class<?> type) {
                final String className = type.getName();
                if (className.startsWith("java.") ||
                        className.startsWith("javax.") ||
                        className.startsWith("android.")) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean acceptMethod(Method method) {
                // With @Subscribe annotation
                if (! method.isAnnotationPresent(Subscribe.class)) {
                    return false;
                }
                // Return type: void
                if (! Void.TYPE.equals(method.getReturnType())) {
                    throw new IllegalArgumentException("Return type of @Subscribe methods must be VOID");
                }
                // Method params
                final Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) {
                    throw new IllegalArgumentException("@Subscribe methods must has single parameter");
                }
                // Check annotation:
                final Annotation[][] annotations = method.getParameterAnnotations();
                if (annotations.length == 0 ||
                        annotations[0].length == 0 ||
                        ! E.class.equals(annotations[0][0].annotationType())) {
                    throw new IllegalArgumentException("Parameter without @E annotation");
                }
                return true;
            }
        });

        // Filter methods and register them
        final MethodSubscriber.Args<Event<T>> args = new MethodSubscriber.Args<Event<T>>() {
            @Override
            public Object[] toInvokeArgs(Event<T> input) {
                return new Object[]{input.value};
            }
        };

        synchronized (mRefs) {
            for (final Method method : annotatedMethods) {
                final MethodSubscriber<Event<T>> subscriber = new MethodSubscriber<>(target, method, args);
                final Class<?> defineType = method.getParameterTypes()[0];
                final E event = (E) method.getParameterAnnotations()[0][0];
                final Subscribe subscribe = method.getAnnotation(Subscribe.class);
                final String defineName = event.value();
                if (defineName == null || defineName.isEmpty()) {
                    throw new IllegalArgumentException("Illegal Event name");
                }
                final int scheduleFlags = subscribe.async() ? Schedule.ASYNC : Schedule.MAIN;
                mRefs.put(target, subscriber);
                mReactor.add(Subscription.create1(
                        subscriber, scheduleFlags, new SubscriptionFilter<T>(defineName, defineType)));
            }
        }

        return this;
    }

    public synchronized NextEvents unregister(Object target) {
        final Subscriber<Event<T>> subscriber = mRefs.remove(target);
        if (subscriber != null) {
            mReactor.remove(subscriber);
        }
        return this;
    }

    public NextEvents subscribe(Subscriber<Event<T>> subscriber, int scheduleFlags, String name, Class<?> type) {
        mReactor.add(Subscription.create1(subscriber, scheduleFlags, new SubscriptionFilter<T>(name, type)));
        return this;
    }

    public NextEvents unsubscribe(Subscriber<Event<T>> subscriber) {
        mReactor.remove(subscriber);
        return this;
    }

    public NextEvents emit(String name, T value) {
        mReactor.emit(new Event<>(name, value));
        return this;
    }

    public NextEvents subscribeOn(Schedule schedule) {
        mReactor.subscribeOn(schedule);
        return this;
    }

    public void close() {
        mReactor.close();
    }

}
