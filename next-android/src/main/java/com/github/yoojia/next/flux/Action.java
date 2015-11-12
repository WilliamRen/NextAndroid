package com.github.yoojia.next.flux;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import com.github.yoojia.next.lang.QuantumObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import static com.github.yoojia.next.lang.Preconditions.notEmpty;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Action {

    private final QuantumObject<String> mSenderStack = new QuantumObject<>();

    public final String type;
    public final Bundle data;

    private Action(String type, Bundle data) {
        notEmpty(type, "Action.type must not be null or empty !");
        this.type = type;
        this.data = data;
    }

    /**
     * 获取Action被emit前的调用方法栈.
     * @return 方法栈调用过程文本描述
     */
    public String getSenderStack(){
        return mSenderStack.get();
    }

    /**
     * hide for flux
     */
    void setSenderStack(String senderStack) {
        mSenderStack.set(senderStack);
    }

    @Override
    public String toString() {
        return "{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }

    public static Action create(String eventName) {
        return new Action.Builder().setType(eventName).build();
    }

    public static class Builder {

        public final Bundle data = new Bundle();
        private String mType;

        public Builder setType(String type){
            mType = type;
            return this;
        }

        public Builder putInt(String key, int value) {
            data.putInt(key, value);
            return this;
        }

        public Builder putLong(String key, long value) {
            data.putLong(key, value);
            return this;
        }

        public Builder putFloat(String key, float value) {
            data.putFloat(key, value);
            return this;
        }

        public Builder putDouble(String key, double value) {
            data.putDouble(key, value);
            return this;
        }

        public Builder putBoolean(String key, boolean value) {
            data.putBoolean(key, value);
            return this;
        }

        public Builder putString(String key, String value) {
            data.putString(key, value);
            return this;
        }

        public Builder putBundle(String key, Bundle value) {
            data.putBundle(key, value);
            return this;
        }

        public Builder putArrayList(String key, ArrayList<? extends Parcelable> value) {
            data.putParcelableArrayList(key, value);
            return this;
        }

        public Builder putSerializable(String key, Serializable value) {
            data.putSerializable(key, value);
            return this;
        }

        public int getInt(String key, int defaultValue) {
            return data.getInt(key, defaultValue);
        }

        public int getInt(String key) {
            return data.getInt(key);
        }

        public long getLong(String key, long defaultValue) {
            return data.getLong(key, defaultValue);
        }

        public long getLong(String key) {
            return data.getLong(key);
        }

        public float getFloat(String key, float defaultValue) {
            return data.getFloat(key, defaultValue);
        }

        public float getFloat(String key) {
            return data.getFloat(key);
        }

        public double getDouble(String key, double defaultValue) {
            return data.getDouble(key, defaultValue);
        }

        public double getDouble(String key) {
            return data.getDouble(key);
        }

        public String getString(String key, String defaultValue) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return data.getString(key, defaultValue);
            }else{
                final String value = data.getString(key);
                return value == null ? defaultValue : value;
            }
        }

        public String getString(String key) {
            return data.getString(key);
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            return data.getBoolean(key, defaultValue);
        }

        public boolean getBoolean(String key){
            return data.getBoolean(key);
        }

        public Bundle getBundle(String key, Bundle defaultValue) {
            final Bundle value = data.getBundle(key);
            return value == null ? defaultValue : value;
        }

        public Bundle getBundle(String key) {
            return getBundle(key, new Bundle(0));
        }

        public ArrayList<? extends Parcelable> getArrayList(String key, ArrayList<? extends Parcelable> defaultValue) {
            ArrayList<? extends Parcelable> value =  data.getParcelableArrayList(key);
            return value == null ? defaultValue : value;
        }

        public ArrayList<? extends Parcelable> getArrayList(String key) {
            return getArrayList(key, new ArrayList<Parcelable>(0));
        }

        public Serializable getSerializable(String key, Serializable defaultValue) {
            final Serializable value = data.getSerializable(key);
            return value == null ? defaultValue : value;
        }

        public Serializable getSerializable(String key) {
            return data.getSerializable(key);
        }

        public Action build(){
            return new Action(mType, data);
        }
    }

}
