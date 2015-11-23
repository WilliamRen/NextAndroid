package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Subscriber;

import java.lang.reflect.Method;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-06
 */
class MethodSubscriber<T> implements Subscriber<T>{

    private final Object mObject;
    private final Method mMethod;
    private final Args<T> mArgs;

    public MethodSubscriber(Object object, Method method, Args<T> args) {
        mObject = object;
        mMethod = method;
        mArgs = args;
    }

    @Override
    public void onCall(T input) throws Exception {
        mMethod.setAccessible(true);
        mMethod.invoke(mObject, mArgs.toInvokeArgs(input));
    }

    @Override
    public void onErrors(Exception errors) {
        throw new RuntimeException(errors);
    }

    public interface Args<T> {
        Object[] toInvokeArgs(T input);
    }
}
