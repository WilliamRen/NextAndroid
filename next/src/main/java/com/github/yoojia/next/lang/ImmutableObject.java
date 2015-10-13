package com.github.yoojia.next.lang;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class ImmutableObject<T> {

    private final Object mLock = new Object();
    private T mObject;

    public ImmutableObject() {
    }

    public ImmutableObject(T initValue) {
        setOnce(initValue);
    }

    public void setOnce(T value) {
        synchronized (mLock) {
            if (mObject == null) {
                mObject = value;
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
