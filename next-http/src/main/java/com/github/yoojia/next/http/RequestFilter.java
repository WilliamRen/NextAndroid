package com.github.yoojia.next.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public interface RequestFilter {

    void onClientCreated(OkHttpClient client);

    void onEachRequest(Request.Builder request);
}
