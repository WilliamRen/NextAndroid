package com.github.yoojia.next.http;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public interface NextCallback {

    void onStart();

    void onResponse(int code, String text);

    void onErrors(Throwable errors);

    void onEnd();
}
