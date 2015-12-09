package com.github.yoojia.next.events;

import android.text.TextUtils;
import android.util.Log;

import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.lang.MethodsFinder;
import com.github.yoojia.next.react.Reactor;
import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;
import com.github.yoojia.next.react.Subscriber;
import com.github.yoojia.next.react.Subscription;

import java.lang.annotation.Annotation;
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

    private final Reactor<EventMeta> mReactor;
    private final Map<Object, ArrayList<Subscriber<EventMeta>>> mRefs = new ConcurrentHashMap<>();

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
     * @param target Target object which should contains methods with @Subscribe.
     * @param customFilter Nullable, to filter accepted methods.
     * @return NextEvent
     * @throws IllegalStateException If target has been registered before
     * @throws IllegalArgumentException
     * If methods with @Subscribe annotation in target object is not matched belows:
     *  - VOID return type
     *  - SINGLE & REQUIRED parameter
     *  - WITH @Evt in parameter
     *  - NOT-EMPTY in @Evt.value
     */
    public NextEvents register(final Object target, final Filter<Method> customFilter) {
        notNull(target, "Target Object must not be null !");
        if (mRefs.containsKey(target)) {
            throw new IllegalStateException("Target object was REGISTERED! " +
                    "<NextEvents.register(...)> and <NextEvents.unregister(...)> must be call in pairs !");
        }
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
        final List<Method> annotatedMethods = new MethodsFinder()
                .filter(newMethodFilter(customFilter))
                .find(target.getClass());
        if (annotatedMethods.isEmpty()) {
            Log.e(TAG, "- Empty Methods(with @Subscribe)! Object host: " + target);
            Warning.show(TAG);
            return this;
        }
        for (final Method method : annotatedMethods) {
            final Evt event = (Evt) method.getParameterAnnotations()[0][0];
            final String defineName = event.value();
            if (TextUtils.isEmpty(defineName)) {
                throw new IllegalArgumentException("Event name in @Evt must not be empty");
            }
            final Subscribe subscribe = method.getAnnotation(Subscribe.class);
            final MethodSubscriber subscriber = new MethodSubscriber(mReactor, target, method);
            subscribers.add(subscriber);
            final Class<?> defineType = method.getParameterTypes()[0];
            this.subscribe(defineName, defineType, subscriber, subscribe.runOn().scheduleFlag);
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
    public NextEvents subscribe(String defineName, Class<?> defineType, Subscriber<EventMeta> subscriber, int flag) {
        notNull(subscriber);
        notEmpty(defineName, "Event name cannot be null or empty");
        notNull(defineType);
        mReactor.add(Subscription.create1(subscriber, flag, EventsFilter.with(defineName, defineType)));
        return this;
    }

    /**
     * Unsubscribe a Subscriber
     * @param subscriber Subscriber
     * @return NextEvents
     * @throws NullPointerException If subscriber is null
     */
    public NextEvents unsubscribe(Subscriber<EventMeta> subscriber) {
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
        mReactor.emit(EventMeta.with(eventName, eventObject));
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

    private static Filter<Method> newMethodFilter(final Filter<Method> customFilter) {
        return new Filter<Method>() {
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
        };
    }

}
