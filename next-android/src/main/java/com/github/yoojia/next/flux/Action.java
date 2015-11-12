package com.github.yoojia.next.flux;

import android.os.Bundle;

import com.github.yoojia.next.lang.QuantumObject;

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

        public Builder putString(String key, String value) {
            data.putString(key, value);
            return this;
        }

        public Action build(){
            return new Action(mType, data);
        }
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
}
