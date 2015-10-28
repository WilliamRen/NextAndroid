package com.github.yoojia.next.views;

import android.util.Log;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
class Warning {

    public static void show(String tag){
        Log.e(tag, "- Seems something wrong in release mode? " +
                        "Try add the belows to your [proguard-rules.pro] file: " +
                        "-keepclassmembers class * {" +
                        "    @com.github.yoojia.next.views.AutoView *;" +
                        "}"
        );
    }
}
