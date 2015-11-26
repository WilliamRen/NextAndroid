package com.github.yoojia.next.inputs;

public abstract class ValuesProxy<T> {

    protected abstract T value0();

    protected T value1(){
        return null;
    }

    protected abstract T valueOf(String input);

    protected abstract boolean test(T input, T value0, T value1);
}