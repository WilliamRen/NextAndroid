package com.github.yoojia.next.events;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public interface OnErrorsListener {
    /**
     * 在执行目标时发生错误
     * @param exception 错误
     */
    void onErrors(EventsException exception);
}
