package com.github.yoojia.next.events;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public class EventsFlags {

    static boolean PERFORMANCE = false;

    static boolean PROCESSING = false;

    /**
     * @param enabled 是否开启性能日志输出
     */
    public static void enabledPerformanceLog(boolean enabled) {
        PERFORMANCE = enabled;
    }

    /**
     * @param enabled 是否开启处理过程日志输出
     */
    public static void enabledProcessingLog(boolean enabled) {
        PROCESSING = enabled;
    }
}
