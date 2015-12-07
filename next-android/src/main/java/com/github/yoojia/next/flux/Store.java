package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public abstract class Store<T> {

    protected final Dispatcher mDispatcher;
    protected final T mContext;

    protected Store(Dispatcher dispatcher) {
        this(dispatcher, null);
    }

    protected Store(Dispatcher dispatcher, T context) {
        mDispatcher = dispatcher;
        mContext = context;
    }

    public void register(){
        mDispatcher.register(this);
    }

    public void unregister(){
        mDispatcher.unregister(this);
    }

    public void emit(Action action){
        mDispatcher.emit(action);
    }

}
