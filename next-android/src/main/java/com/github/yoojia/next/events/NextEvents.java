package com.github.yoojia.next.events;

import android.util.Log;

import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.lang.MethodsFinder;
import com.github.yoojia.next.react.OnTargetMissListener;
import com.github.yoojia.next.react.Reactor;
import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;
import com.github.yoojia.next.react.Subscriber;
import com.github.yoojia.next.react.Subscriptions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.yoojia.next.lang.Preconditions.notEmpty;
import static com.github.yoojia.next.lang.Preconditions.notNull;

/**
 * NextEvents
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public class NextEvents {

    private static final String TAG = "NextEvents";

    private final Reactor<Meta> mReactor;
    private final Map<Object, InvokableMethods> mObjectMaps = new ConcurrentHashMap<>();

    /**
     * Create a NextEvents instance, using default(shared threads) schedule for subscribers.
     */
    public NextEvents() {
        this(Schedules.sharedThreads());
    }

    /**
     * Create a NextEvents instance with Schedule for subscribers.
     * @param subscribeOn Schedule for subscribers.
     * @throws NullPointerException If schedule is null.
     */
    public NextEvents(Schedule subscribeOn) {
        notNull(subscribeOn);
        mReactor = new Reactor<>(subscribeOn);
    }

    /**
     * Register and scan methods with @Subscriber in target object, and accept a filter to
     * filter accepted methods.
     * @param object Target object which should contains methods with @Subscribe.
     * @param customFilter Nullable, to filter accepted methods.
     * @return NextEvent
     * @throws IllegalArgumentException If methods with @Subscribe annotation in target object match one of belows:
     *  - NOT VOID return type
     *  - NOT SINGLE AND REQUIRED parameter
     *  - NOT WITH @Evt in parameter
     *  - EMPTY in @Evt.value
     */
    public NextEvents register(final Object object, final Filter<Method> customFilter) {
        notNull(object, "Target object must not be null");
        // Filter methods and register them
        final InvokableMethods invokable;
        // if not registered, put to register
        if ( ! mObjectMaps.containsKey(object)) {
            invokable = new InvokableMethods();
            mObjectMaps.put(object, invokable);
        }else{
            invokable = mObjectMaps.get(object);
        }
        final List<Method> annotatedMethods = new MethodsFinder()
                .filter(newMethodFilter(customFilter))
                .find(object.getClass());
        if (annotatedMethods.isEmpty()) {
            Log.e(TAG, "- Empty Methods(with @Subscribe)! Target: " + object);
            Warning.show(TAG);
        }else{
            for (final Method method : annotatedMethods) {
                checkSignature(method);
                if (invokable.notContains(method)) {
                    subscribeTargetMethod(object, method, invokable);
                }
            }
        }
        return this;
    }

    /**
     * Unregister the object, all methods with @Subscribe in this object will be remove from NextEvents.
     * @param target Target to unregister;
     * @return NextEvents
     * @throws NullPointerException If target to unregister is null;
     * @throws IllegalStateException If target was not registered before;
     */
    public synchronized NextEvents unregister(Object target) {
        notNull(target);
        final InvokableMethods invokable = mObjectMaps.remove(target);
        if (invokable != null) {
            for (Subscriber<Meta> sub : invokable) {
                unsubscribe(sub);
            }
        }
        return this;
    }

    /**
     * Register a subscriber.
     * @param defineName Event name for subscriber;
     * @param defineType Event type for subscriber;
     * @param subscriber Subscriber;
     * @param flag Schedule Flag for subscriber;
     * @return NextEvents
     * @throws NullPointerException
     * - If subscriber is null;
     * - If class type is null;
     * @throws IllegalArgumentException If event name is null or empty
     * @throws IllegalStateException If the subscriber was registered before
     */
    public NextEvents subscribe(String defineName, Class<?> defineType, Subscriber<Meta> subscriber, int flag) {
        notNull(subscriber);
        notEmpty(defineName, "Event name cannot be null or empty");
        notNull(defineType);
        mReactor.add(Subscriptions.create1(subscriber, flag, EventsFilter.with(defineName, defineType)));
        return this;
    }

    /**
     * Unsubscribe a Subscriber
     * @param subscriber Subscriber
     * @return NextEvents
     * @throws NullPointerException If subscriber is null
     */
    public NextEvents unsubscribe(Subscriber<Meta> subscriber) {
        notNull(subscriber);
        mReactor.remove(subscriber);
        return this;
    }

    /**
     * Emit a event
     * @param eventName Event name
     * @param eventObject Event value object
     * @return NextEvents
     * @throws NullPointerException If event name or event object is null
     */
    public NextEvents emit(String eventName, Object eventObject) {
        notNull(eventName);
        notNull(eventObject);
        mReactor.emit(Meta.with(eventName, eventObject));
        return this;
    }

    /**
     * Set a schedule impl for NextEvents
     * @param schedule Schedule
     * @return NextEvents
     * @throws NullPointerException If schedule is null
     */
    public NextEvents subscribeOn(Schedule schedule) {
        notNull(schedule);
        mReactor.subscribeOn(schedule);
        return this;
    }

    /**
     * Set event listener
     * @param listener Listener
     * @return NextEvents
     * @throws NullPointerException If listener is null
     */
    public NextEvents setOnTargetMissListener(OnTargetMissListener<Meta> listener){
        notNull(listener);
        mReactor.onEventListener(listener);
        return this;
    }

    /**
     * 提供可Override的访问权限，给子类改写默认处理过程的可能性
     * @param object Object
     * @param method Method with annotation
     * @param invokable Invokable
     */
    protected void subscribeTargetMethod(Object object, Method method, InvokableMethods invokable) {
        final Subscribe subscribe = method.getAnnotation(Subscribe.class);
        final MethodSubscriber subscriber = new MethodSubscriber(object, method);
        invokable.add(subscriber);
        final String defineName = subscribe.on();
        final Class<?> defineType = method.getParameterTypes()[0];
        subscribe(defineName, defineType, subscriber, subscribe.run().scheduleFlag);
    }

    protected Filter<Method> newMethodFilter(final Filter<Method> customFilter) {
        return new Filter<Method>() {
            @Override public boolean accept(Method method) {
                if ( ! isSubscribeMethod(method)) {
                    return false;
                }else{
                    return customFilter == null || customFilter.accept(method);
                }
            }
        };
    }

    protected boolean isSubscribeMethod(Method method) {
        if (method.isBridge() || method.isSynthetic()) {
            return false;
        }
        if (! method.isAnnotationPresent(Subscribe.class)) {
            return false;
        }
        return true;
    }

    private static void checkSignature(Method method){
        if (! Void.TYPE.equals(method.getReturnType())) {
            throw new IllegalArgumentException("Return type of @Subscribe annotated methods must be <VOID> , method: " + method);
        }
        final Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            throw new IllegalArgumentException("@Subscribe annotated methods must has a single param , method: " + method);
        }
    }

    public static class InvokableMethods extends ArrayList<MethodSubscriber> {

        public boolean notContains(Method method) {
            for (MethodSubscriber ms : this) {
                if (ms.isSameWith(method)) {
                    return false;
                }
            }
            return true;
        }
    }
}
