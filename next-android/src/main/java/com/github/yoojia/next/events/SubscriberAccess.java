package com.github.yoojia.next.events;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
interface SubscriberAccess<T extends Subscriber> {

    void access(T newSubscriber);
}
