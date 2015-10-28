package com.github.yoojia.next.events;

import android.util.Log;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
class Warning {

    public static void show(String tag){
        Log.e(tag, "- Seems something wrong in release mode? " +
                "Try add the belows to your [proguard-rules.pro] file: " +
                "-keepclassmembers class * {" +
                "    @com.github.yoojia.next.events.Subscribe *;" +
                "    @com.github.yoojia.next.events.Event *;" +
                "}"
        );
    }
}
