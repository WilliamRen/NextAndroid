package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
@Deprecated
public abstract class Store<T> extends AbstractStore<T>{

    protected Store(Dispatcher dispatcher) {
        super(dispatcher);
    }

    protected Store(Dispatcher dispatcher, T context) {
        super(dispatcher, context);
    }
}
