package com.github.yoojia.next.events;

import android.util.Log;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
class Logger {

    public static void timeLog(String tag, String message, long start) {
        if (!EventsFlags.PERFORMANCE) return;
        final float delta = (System.nanoTime() - start) / 1000000.0f;
        final String time = String.format("%.3f", delta)  + "ms";
        Log.d(tag, "[" + time + "] " + message);
    }
}
