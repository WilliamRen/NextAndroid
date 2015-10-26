package com.github.yoojia.next.lang;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class QuantumObject<T> {

    private final Object mLock = new Object();
    private T mObject;

    public QuantumObject() {
    }

    public QuantumObject(T initValue) {
        set(initValue);
    }

    public void set(T value) {
        synchronized (mLock) {
            if (mObject == null) {
                mObject = value;
            }else{
                throw new IllegalStateException("Value had set !");
            }
        }
    }

    public T get(){
        synchronized (mLock){
            return mObject;
        }
    }

    public boolean watch() {
        synchronized (mLock) {
            return null != mObject;
        }
    }
}
