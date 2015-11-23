package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Reactor;
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

    private final Reactor mReactorRef;

    public MethodSubscriber(Reactor reactorRef, Object object, Method method, Args<T> args) {
        mReactorRef = reactorRef;
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
    @SuppressWarnings("unchecked")
    public void onErrors(Exception errors) {
        // @Subscribe 方法发生错误时，将错误包装成ExceptionEvent，再转发
        mReactorRef.emit(new EventMeta<>(ExceptionEvent.NAME, new ExceptionEvent(errors)));
    }

    public interface Args<T> {
        Object[] toInvokeArgs(T input);
    }
}
