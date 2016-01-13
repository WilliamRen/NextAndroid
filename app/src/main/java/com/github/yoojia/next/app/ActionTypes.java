package com.github.yoojia.next.app;

import com.github.yoojia.next.flux.Action;

public class ActionTypes {

    public static final String RAW_MESSAGES = "raw-messages";
    public static final String CHANGED_MESSAGES = "changed-messages";

    public static Action createRawMessage(LongMessage msg) {
        return new Action(RAW_MESSAGES, msg);
    }

    public static Action createChangedMessages(LongMessage msg) {
        return new Action(CHANGED_MESSAGES, msg);
    }
}
