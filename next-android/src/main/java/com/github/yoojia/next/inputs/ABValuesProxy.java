package com.github.yoojia.next.inputs;

public abstract class ABValuesProxy<T> {

    protected abstract T valueA();

    protected T valueB(){
        return null;
    }

    protected abstract T valueOf(String input);

    protected abstract boolean test(T input, T value0, T value1);
}