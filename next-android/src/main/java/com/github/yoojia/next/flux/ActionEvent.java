package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class ActionEvent {

    final String event;
    final Action action;

    public ActionEvent(String event, Action action) {
        this.event = event;
        this.action = action;
    }

}
