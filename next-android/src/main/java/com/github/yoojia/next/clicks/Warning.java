package com.github.yoojia.next.clicks;

import android.util.Log;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
class Warning {

    public static void show(String tag){
        Log.e(tag, "- Seems something wrong in release mode? Try add belows configs to your [proguard-rules.pro] file: ");
        Log.e(tag, "\n-keepclassmembers class * {\n" +
                "    @com.github.yoojia.next.clicks.EmitClick *;\n" +
                "}");
    }
}
