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
        mDispatcher.register(this);
    }

    public void dispatch(Action action) {
        mDispatcher.dispatch(action);
    }

    public T getContext() {
        return mContext;
    }

    public Dispatcher getDispatcher() {
        return mDispatcher;
    }

    public void connect(Object target) {
        mDispatcher.register(target);
    }
}
