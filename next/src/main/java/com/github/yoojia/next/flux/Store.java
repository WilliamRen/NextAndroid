package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public abstract class Store {

    protected final Dispatcher mDispatcher;

    protected Store(Dispatcher dispatcher) {
        mDispatcher = dispatcher;
    }

    public void register(){
        mDispatcher.registerWithStopType(this, Store.class);
    }

    public void unregister(){
        mDispatcher.unregister(this);
    }

    public void emit(Action action, String eventName){
        mDispatcher.emit(action, eventName);
    }

}
