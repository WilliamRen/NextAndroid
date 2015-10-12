package com.github.yoojia.next.lang;

import android.util.SparseArray;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public class ObjectMap {

    private final SparseArray<Object> mData = new SparseArray<>();

    public ObjectMap put(String key, Object value){
        mData.put(Objects.hash(key), value);
        return this;
    }

    public Object get(String key, Object defValue){
        final Object value = mData.get(Objects.hash(key));
        if (value == null) {
            return defValue;
        }else{
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getTyped(String key, T defValue) {
        try{
            return (T) get(key, defValue);
        }catch (ClassCastException e) {
            e.printStackTrace();
            return defValue;
        }
    }

    public String getString(String key){
        return getTyped(key, null);
    }

    public int getInt(String key){
        return getTyped(key, 0);
    }

    public long getLong(String key){
        return getTyped(key, 0L);
    }

    public float getFloat(String key){
        return getTyped(key, 0f);
    }

    public double getDouble(String key){
        return getTyped(key, 0.0);
    }

    public boolean getBoolean(String key){
        return getTyped(key, false);
    }

    @Override
    public String toString() {
        return mData.toString();
    }
}
