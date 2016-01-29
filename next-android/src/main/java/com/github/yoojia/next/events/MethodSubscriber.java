package com.github.yoojia.next.events;

import com.github.yoojia.next.events.supports.Subscriber;

import java.lang.reflect.Method;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-06
 */
public class MethodSubscriber implements Subscriber<Meta>{

    private final Object mObject;
    private final Method mMethod;

    public MethodSubscriber(Object object, Method method) {
        mObject = object;
        mMethod = method;
    }

    @Override
    public void onCall(Meta input) throws Exception {
        mMethod.setAccessible(true);
        mMethod.invoke(mObject, input.value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onErrors(Meta input, Exception errors) {
        throw new RuntimeException(input.toString(), errors);
    }

    boolean isSameMethod(Method method) {
        return mMethod.equals(method);
    }
}
