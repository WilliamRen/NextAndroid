package com.github.yoojia.next.lang;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class ObjectWrap<T> {

    private T mObject;

    public ObjectWrap() {
    }

    public ObjectWrap(T initValue) {
        set(initValue);
    }

    public synchronized void set(T value) {
        if (mObject == null) {
            mObject = value;
        }else{
            throw new IllegalStateException("Value has set !");
        }
    }

    public synchronized T get(){
        return mObject;
    }

    public boolean has() {
        return null != mObject;
    }

    public static <T> ObjectWrap<T> wrap(T object) {
        return new ObjectWrap<>(object);
    }
}
