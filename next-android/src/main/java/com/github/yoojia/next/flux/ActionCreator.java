package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class ActionCreator {

    public static ActionEvent createRawMessage(String type, Message payload) {
        return new ActionEvent(ActionTypes.RAW_MESSAGES, new Action(type, payload));
    }

    public static ActionEvent createChangedMessage(String type, Message payload) {
        return new ActionEvent(ActionTypes.CHANGED_MESSAGES, new Action(type, payload));
    }
}
