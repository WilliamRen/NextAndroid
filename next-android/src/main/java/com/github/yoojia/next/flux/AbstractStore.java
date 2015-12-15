package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public abstract class AbstractStore<T> {

    protected final Dispatcher mDispatcher;
    protected final T mContext;

    protected AbstractStore(Dispatcher dispatcher) {
        this(dispatcher, null);
    }

    protected AbstractStore(Dispatcher dispatcher, T context) {
        mDispatcher = dispatcher;
        mContext = context;
    }

    public void register(Object target){
        mDispatcher.register(this);
        mDispatcher.register(target);
    }

    public void unregister(Object target){
        mDispatcher.unregister(this);
        mDispatcher.unregister(target);
    }

    @Deprecated
    public void emit(Action action){
        dispatch(action);
    }

    public void dispatch(Action action) {
        mDispatcher.dispatch(action);
    }
}
