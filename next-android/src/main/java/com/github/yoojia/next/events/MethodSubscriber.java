package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Reactor;
import com.github.yoojia.next.react.Subscriber;

import java.lang.reflect.Method;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-06
 */
class MethodSubscriber implements Subscriber<EventMeta>{

    private final Object mObject;
    private final Method mMethod;

    private final Reactor mReactorRef;

    public MethodSubscriber(Reactor reactorRef, Object object, Method method) {
        mReactorRef = reactorRef;
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
        // @Subscribe 方法发生错误时，将错误包装成ExceptionEvent，再转发
        mReactorRef.emit(new EventMeta(ExceptionEvent.NAME,
                new ExceptionEvent(errors, mMethod.getName(), input.name, input.value)));
    }

}
