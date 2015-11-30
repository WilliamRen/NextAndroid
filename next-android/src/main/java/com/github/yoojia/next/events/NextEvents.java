package com.github.yoojia.next.events;

import android.text.TextUtils;
import android.util.Log;

import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.lang.MethodsFinder;
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

import static com.github.yoojia.next.lang.Preconditions.notNull;


/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
public class NextEvents {

    private static final String TAG = "NextEvents";

    private final Reactor<EventMeta> mReactor = new Reactor<>();
    private final Map<Object, ArrayList<Subscriber<EventMeta>>> mRefs = new ConcurrentHashMap<>();

    /**
     * 从目标对象中注册添加@Subscribe注解的Methods, 并指定Method的过滤接口.
     * @param target 包含@Subscribe注解的目标对象
     * @param customFilter 指定Method过滤接口
     * @return NextEvent
     */
    public NextEvents register(final Object target, final Filter<Method> customFilter) {
        notNull(target, "Target Object must not be null !");
        if (mRefs.containsKey(target)) {
            throw new IllegalStateException("Target object was REGISTERED! " +
                    "<NextEvents.register(...)> and <NextEvents.unregister(...)> must be call in pairs !");
        }
        // Find @Subscribe methods
        final List<Method> annotatedMethods = new MethodsFinder()
                .filter(new Filter<Method>() {
                    @Override public boolean accept(Method method) {
                        if (method.isBridge() || method.isSynthetic()) {
                            return false;
                        }
                        // With @Subscribe annotation
                        if (! method.isAnnotationPresent(Subscribe.class)) {
                            return false;
                        }
                        // Return type: void
                        if (! Void.TYPE.equals(method.getReturnType())) {
                            throw new IllegalArgumentException("Return type of @Subscribe annotated methods must be VOID" +
                                    ", method: " + method);
                        }
                        // Method params
                        final Class<?>[] params = method.getParameterTypes();
                        if (params.length != 1) {
                            throw new IllegalArgumentException("@Subscribe annotated methods must have a single parameter" +
                                    ", method: " + method);
                        }
                        // Check annotation:
                        final Annotation[][] annotations = method.getParameterAnnotations();
                        if (annotations.length == 0 ||
                                annotations[0].length == 0 ||
                                ! Evt.class.equals(annotations[0][0].annotationType())) {
                            throw new IllegalArgumentException("The parameter without @Evt annotation" +
                                    ", method" + method);
                        }
                        // custom filter
                        return customFilter == null || customFilter.accept(method);
                    }
                })
                .find(target.getClass());
        // Filter methods and register them
        final ArrayList<Subscriber<EventMeta>> subscribers;
        // if not registered, add to Refs(register)
        if ( ! mRefs.containsKey(target)) {
            subscribers = new ArrayList<>();
            mRefs.put(target, subscribers);
        }else{
            subscribers = mRefs.get(target);
        }
        // Check Annotations methods
        if (annotatedMethods.isEmpty()) {
            Log.e(TAG, "- Empty Methods(with @Subscribe)! Object host: " + target);
            Warning.show(TAG);
            return this;
        }
        for (final Method method : annotatedMethods) {
            final Evt event = (Evt) method.getParameterAnnotations()[0][0];
            final String defineName = event.value();
            if (TextUtils.isEmpty(defineName)) {
                throw new IllegalArgumentException("Event name in @Subscribe must not be empty");
            }
            final Subscribe subscribe = method.getAnnotation(Subscribe.class);
            final int flags = subscribe.onThreads() ? Schedule.FLAG_ON_THREADS : Schedule.FLAG_ON_MAIN;
            final MethodSubscriber subscriber = new MethodSubscriber(mReactor, target, method);
            subscribers.add(subscriber);
            final Class<?> defineType = method.getParameterTypes()[0];
            this.subscribe(subscriber, flags, defineName, defineType);
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
            throw new IllegalStateException("Target object was NOT REGISTERED! " +
                    "<NextEvents.register(...)> and <NextEvents.unregister(...)> must be call in pairs !");
        }else{// registered
            final ArrayList<Subscriber<EventMeta>> subscribers = mRefs.remove(target);
            for (Subscriber<EventMeta> subscriber : subscribers) {
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
    public NextEvents subscribe(Subscriber<EventMeta> subscriber, int scheduleFlags, String defineName, Class<?> defineType) {
        mReactor.add(Subscription.create1(subscriber, scheduleFlags, new EventsFilter(defineName, defineType)));
        return this;
    }

    /**
     * 反注册Subscriber
     * @param subscriber Subscriber
     * @return NextEvents
     */
    public NextEvents unsubscribe(Subscriber<EventMeta> subscriber) {
        mReactor.remove(subscriber);
        return this;
    }

    /**
     * 发布事件
     * @param eventName 事件名
     * @param eventObject 事件对象
     * @return NextEvents
     */
    public NextEvents emit(String eventName, Object eventObject) {
        mReactor.emit(new EventMeta(eventName, eventObject));
        return this;
    }

    /**
     * 指定订阅执行目标的调度器
     * @param schedule 调度器
     * @return NextEvents
     */
    public NextEvents subscribeOn(Schedule schedule) {
        mReactor.subscribeOn(schedule);
        return this;
    }

    /**
     * 努力争取去掉这个方法
     */
    public void close() {
        mReactor.close();
    }

}
