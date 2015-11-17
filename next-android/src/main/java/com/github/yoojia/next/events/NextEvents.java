package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Reactor;
import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Subscriber;
import com.github.yoojia.next.react.Subscription;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
public class NextEvents<T> {

    private final Reactor<Event<T>> mReactor = new Reactor<>();
    private final Map<Object, ArrayList<Subscriber<Event<T>>>> mRefs = new ConcurrentHashMap<>();

    /**
     * 从目标对象中注册添加@Subscribe注解的Methods, 并指定Method的过滤接口.
     * @param target 包含@Subscribe注解的目标对象
     * @param customFilter 指定Method过滤接口
     * @return NextEvent实例
     */
    public NextEvents register(final Object target, final MethodFinder.Filter customFilter) {
        if (mRefs.containsKey(target)) {
            throw new IllegalStateException("Target object was REGISTERED! " +
                    "NextEvents.register(...) $ NextEvents.unregister(...) must call in pairs !");
        }
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
                // custom filter
                return customFilter == null || customFilter.acceptType(type);
            }

            @Override
            public boolean acceptMethod(Method method) {
                // With @Subscribe annotation
                if (! method.isAnnotationPresent(Subscribe.class)) {
                    return false;
                }
                // Return type: void
                if (! Void.TYPE.equals(method.getReturnType())) {
                    throw new IllegalArgumentException("Return type of @Subscribe annotated methods must be VOID");
                }
                // Method params
                final Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) {
                    throw new IllegalArgumentException("@Subscribe annotated methods must has single parameter");
                }
                // Check annotation:
                final Annotation[][] annotations = method.getParameterAnnotations();
                if (annotations.length == 0 ||
                        annotations[0].length == 0 ||
                        ! Evt.class.equals(annotations[0][0].annotationType())) {
                    throw new IllegalArgumentException("Parameter without @Evt annotation");
                }
                // custom filter
                return customFilter == null || customFilter.acceptMethod(method);
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
            final ArrayList<Subscriber<Event<T>>> subscribers;
            if ( ! mRefs.containsKey(target)) {
                subscribers = new ArrayList<>();
                mRefs.put(target, subscribers);
            }else{
                subscribers = mRefs.get(target);
            }
            for (final Method method : annotatedMethods) {
                final MethodSubscriber<Event<T>> subscriber = new MethodSubscriber<>(target, method, args);
                final Class<?> defineType = method.getParameterTypes()[0];
                final Evt event = (Evt) method.getParameterAnnotations()[0][0];
                final Subscribe subscribe = method.getAnnotation(Subscribe.class);
                final String defineName = event.value();
                if (defineName == null || defineName.isEmpty()) {
                    throw new IllegalArgumentException("Illegal Event name");
                }
                final int scheduleFlags = subscribe.async() ? Schedule.ASYNC : Schedule.MAIN;
                subscribers.add(subscriber);
                this.subscribe(subscriber, scheduleFlags, defineName, defineType);
            }
        }

        return this;
    }

    /**
     * 反注册目标对象, 所有这个对象的@Subscribe注解方法将被移出管理
     * @param target 目标对象
     * @return NextEvents实例
     */
    public synchronized NextEvents unregister(Object target) {
        if (! mRefs.containsKey(target)) {
            if (mRefs.containsKey(target)) {
                throw new IllegalStateException("Target object was NOT REGISTERED! " +
                        "NextEvents.register(...) $ NextEvents.unregister(...) must call in pairs !");
            }
        }else{
            final ArrayList<Subscriber<Event<T>>> subscribers = mRefs.remove(target);
            for (Subscriber<Event<T>> subscriber : subscribers) {
                unsubscribe(subscriber);
            }
        }
        return this;
    }

    /**
     * 注册Subscriber, 并指定参数
     * @param subscriber Subscriber
     * @param scheduleFlags 触发回调方式标志
     * @param defineName 接受触发的事件名
     * @param defineType 接受触发的事件类型
     * @return NextEvents
     */
    public NextEvents subscribe(Subscriber<Event<T>> subscriber, int scheduleFlags,
                                String defineName, Class<?> defineType) {
        mReactor.add(Subscription.create1(subscriber, scheduleFlags,
                new AcceptFilter<T>(defineName, defineType)));
        return this;
    }

    /**
     * 返回注册Subscriber
     * @param subscriber Subscriber
     * @return NextEvents
     */
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

    @Deprecated
    public void destroy(){
        close();
    }

}
