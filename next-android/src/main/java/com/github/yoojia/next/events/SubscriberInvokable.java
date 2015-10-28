package com.github.yoojia.next.events;

import java.util.Map;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
class SubscriberInvokable extends Invokable<Subscriber> {

    public SubscriberInvokable(Meta[] events, Subscriber invokable, boolean async) {
        super(events, invokable, async);
    }

    @Override
    public void invoke(Map<String, Object> events) throws Exception {
        invokable.call(events);
    }

    @Override
    public boolean isRemovable(Object other) {
        return invokable == other;
    }
}
