package com.github.yoojia.next.events;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class TestLogger {

    public static void timeLog(String message, long start) {
        final float delta = (System.nanoTime() - start) / 1000000.0f;
        final String time = String.format("%.3f", delta)  + "ms";
        System.err.println("[" + time + "] " + message);
    }
}
