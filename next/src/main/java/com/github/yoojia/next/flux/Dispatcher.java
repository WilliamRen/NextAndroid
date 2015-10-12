package com.github.yoojia.next.flux;

import android.app.Activity;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.UIThreadEvents;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Dispatcher {

    private final NextEvents mEvents;

    public Dispatcher(Class<?> stopAtParentType) {
        mEvents = new UIThreadEvents(Runtime.getRuntime().availableProcessors(), "FluxDispatcher", stopAtParentType);
    }

    public void register(Object host){
        mEvents.register(host);
    }

    public void registerAsync(Object host){
        mEvents.registerAsync(host);
    }

    void registerWithStopType(Object host, Class<?> stopAtParentType){
        mEvents.register(host, stopAtParentType);
    }

    public void unregister(Object host){
        mEvents.unregister(host);
    }

    public void emit(Action action, String eventName){
        mEvents.emit(action, eventName);
    }

    public void shutdown(){
        mEvents.shutdown();
    }

    public static Dispatcher newActivity(){
        return new Dispatcher(Activity.class);
    }
}
