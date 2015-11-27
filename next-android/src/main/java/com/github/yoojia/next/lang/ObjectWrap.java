package com.github.yoojia.next.lang;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class ObjectWrap<T> {

    private final Object mLock = new Object();
    private T mObject;

    public ObjectWrap() {
    }

    public ObjectWrap(T initValue) {
        set(initValue);
    }

    public void set(T value) {
        synchronized (mLock) {
            if (mObject == null) {
                mObject = value;
            }else{
                throw new IllegalStateException("Value has set !");
            }
        }
    }

    public T get(){
        synchronized (mLock){
            return mObject;
        }
    }

    public boolean has() {
        synchronized (mLock) {
            return null != mObject;
        }
    }
}
