package com.github.yoojia.next.flux;

import com.github.yoojia.next.lang.ObjectWrap;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Action {

    final String type;

    public final Message message;
    public final Object extras;

    private final ObjectWrap<String> mSenderStack = new ObjectWrap<>();

    public Action(String type, Message payload, Object extras) {
        this.type = type;
        this.message = payload;
        this.extras = extras;
    }

    public Action(String type, Message message) {
        this(type, message, null);
    }

    public Action(String type) {
        this(type, null);
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

    public Action create(String type, Message message) {
        return new Action(type, message);
    }

    public Action create(String type) {
        return new Action(type);
    }
}
