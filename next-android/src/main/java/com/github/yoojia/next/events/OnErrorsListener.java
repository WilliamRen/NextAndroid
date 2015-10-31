package com.github.yoojia.next.events;

import java.util.Set;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public interface OnErrorsListener {
    /**
     * 在执行目标时发生错误
     * @param errors 事件名
     * @param exception 错误
     */
    void onErrors(Set<String> errors, EventsException exception);
}
