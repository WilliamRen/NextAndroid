package com.github.yoojia.next.flux;

import com.github.yoojia.next.lang.QuantumObject;
import com.github.yoojia.next.lang.ObjectMap;

import static com.github.yoojia.next.lang.Preconditions.notEmpty;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Action {

    private final QuantumObject<String> mSenderStack = new QuantumObject<>();

    public final String type;
    public final ObjectMap data;

    private Action(String type, ObjectMap data) {
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

        private String mType;
        private final ObjectMap mData = new ObjectMap();

        public Builder setType(String type){
            mType = type;
            return this;
        }

        public Builder putData(String key, Object value){
            mData.put(key, value);
            return this;
        }

        public Action build(){
            return new Action(mType, mData);
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
