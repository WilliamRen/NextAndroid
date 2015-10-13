package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public abstract class Store<T> {

    protected final Dispatcher mDispatcher;
    protected final T mContextHost;

    protected Store(Dispatcher dispatcher) {
        this(dispatcher, null);
    }

    protected Store(Dispatcher dispatcher, T contextHost) {
        mDispatcher = dispatcher;
        mContextHost = contextHost;
    }

    public void register(){
        mDispatcher.registerWithStopType(this, Store.class);
    }

    public void unregister(){
        mDispatcher.unregister(this);
    }

    public void emit(Action action){
        mDispatcher.emit(action);
    }

}
