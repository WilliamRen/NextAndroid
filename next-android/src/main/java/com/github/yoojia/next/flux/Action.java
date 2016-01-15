package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Action {

    final String type;

    public final Message message;
    public final Object extras;

    private final StringBuilder mSenderStack = new StringBuilder();

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
        return mSenderStack.toString();
    }

    /**
     * hide for flux
     */
    void setSenderStack(String senderStack) {
        mSenderStack.setLength(0);
        mSenderStack.append(senderStack);
    }

    @Override
    public String toString() {
        return "{" +
                "\"extras\":\"" + extras + "\"" +
                ", \"type:\"" + type + "\"" +
                ", \"message\":\"" + message + "\"" +
                ", \"senders\":\"" + mSenderStack + "\"" +
                '}';
    }

    public static Action create(String type, Message message) {
        return new Action(type, message);
    }

    public static Action create(String type) {
        return new Action(type);
    }
}
