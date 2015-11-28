package com.github.yoojia.next.events;

import android.util.Log;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
class Warning {

    public static void show(String tag){
        final String tip = "@Subscribe methods not found ! Try add belows to your <proguard-rules.pro> file:\n" +
                " -keepclassmembers class * { @com.github.yoojia.next.events.Evt *; @com.github.yoojia.next.events.Subscribe *; }";
        Log.e(tag, tip);
    }
}
