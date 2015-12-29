package com.github.yoojia.next.clicks;

import com.github.yoojia.next.events.MethodSubscriber;
import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.react.Schedule;

import java.lang.reflect.Method;

/**
 * 覆盖NextEvents对@Subscriber注解的处理，并使用@ClickHandler来替换其处理过程
 *
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
class ClickEventsImpl extends NextEvents{

    public ClickEventsImpl(Schedule subscribeOn) {
        super(subscribeOn);
    }

    @Override
    protected boolean isSubscribeMethod(Method method) {
        if (method.isBridge() || method.isSynthetic()) {
            return false;
        }
        if (! method.isAnnotationPresent(ClickHandler.class)) {
            return false;
        }
        return true;
    }

    @Override
    protected void subscribeTargetMethod(Object object, Method method, NextEvents.InvokableMethods invokable) {
        final ClickHandler subscribe = method.getAnnotation(ClickHandler.class);
        final MethodSubscriber subscriber = new MethodSubscriber(object, method);
        invokable.add(subscriber);
        final String defineName = subscribe.on();
        final Class<?> defineType = method.getParameterTypes()[0];
        subscribe(defineName, defineType, subscriber, Schedule.FLAG_ON_CALLER);
    }
}
