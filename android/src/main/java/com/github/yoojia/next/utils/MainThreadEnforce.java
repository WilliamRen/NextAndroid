package com.github.yoojia.next.utils;

import android.os.Looper;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public class MainThreadEnforce {

    /**
     * 确保在主线程中
     * @param message 异常提示
     */
    public static void is(String message){
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * 确保在非主线程中
     * @param message 异常提示
     */
    public static void not(String message) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalStateException(message);
        }
    }
}
