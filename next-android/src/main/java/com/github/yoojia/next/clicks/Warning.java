package com.github.yoojia.next.clicks;

import android.util.Log;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
class Warning {

    public static void show(String tag){
        final String tip = "@ClickEvent fields not found ! Try add belows to your <proguard-rules.pro> file:\n" +
                " -keepclassmembers class * { @com.github.yoojia.next.clicks.ClickEvt *; }";
        Log.e(tag, tip);
    }
}
