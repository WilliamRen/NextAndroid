package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Subscriber;

import java.lang.reflect.Method;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-06
 */
class MethodSubscriber implements Subscriber<EventMeta>{

    private final Object mObject;
    private final Method mMethod;

    public MethodSubscriber(Object object, Method method) {
        mObject = object;
        mMethod = method;
    }

    @Override
    public void onCall(EventMeta input) throws Exception {
        mMethod.setAccessible(true);
        mMethod.invoke(mObject, input.value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onErrors(EventMeta input, Exception errors) {
        throw new RuntimeException("Error on event: " + input);
    }

}
