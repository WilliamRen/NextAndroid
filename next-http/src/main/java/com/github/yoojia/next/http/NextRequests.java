package com.github.yoojia.next.http;

import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextRequests {

    private static final String TAG = "REQUESTS";

    private static final String SCHEME_FILE = "file://";
    private static final String SCHEME_RAW = "REQUEST#RAW-POST://";

    private static final MediaType MEDIA_TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("plain/text; charset=utf-8");

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    private static final CookieManager COOKIES = new CookieManager();
    private static final ExecutorService THREADS = Executors.newCachedThreadPool();

    private final RequestFilter mRequestFilter;
    private final String mBaseURL;
    private final Map<String, Object> mParams = new HashMap<>();

    private boolean mPOST = false;
    private String mTargetURL;

    public NextRequests(String baseURL, RequestFilter requestFilter) {
        mBaseURL = baseURL;
        mTargetURL = baseURL;
        mRequestFilter = requestFilter;
    }

    public NextRequests post(String uri) {
        return post(uri, Collections.EMPTY_MAP);
    }

    public NextRequests post(String uri, String jsonText){
        return post(uri, jsonText, "application/json; charset=utf-8");
    }

    public NextRequests post(String uri, String content, String contentType){
        final Map<String, Object> params = new HashMap<>(2);
        params.put(SCHEME_RAW + "content", content);
        params.put(SCHEME_RAW + "type", contentType);
        return post(uri, params);
    }

    public NextRequests post(String uri, Map<String, Object> params){
        mTargetURL = mBaseURL + uri;
        return post(params);
    }

    public NextRequests post(Map<String, Object> params) {
        mPOST = true;
        mParams.clear();
        if (params != null && !params.isEmpty()){
            mParams.putAll(params);
        }
        return this;
    }

    public NextRequests get(String uri) {
        return get(uri, Collections.EMPTY_MAP);
    }

    public NextRequests get(String uri, Map<String, Object> params){
        mTargetURL = mBaseURL + uri;
        return get(params);
    }

    public NextRequests get(Map<String, Object> params) {
        mPOST = false;
        mParams.clear();
        if (params != null && !params.isEmpty()){
            mParams.putAll(params);
        }
        return this;
    }

    public void go(final NextCallback callback){
        THREADS.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendRequest(callback);
                } catch (Throwable errors) {
                    callback.onErrors(errors);
                } finally {
                    callback.onEnd();
                }
            }
        });
    }

    private void sendRequest(final NextCallback callback) throws Exception{
        callback.onStart();
        if (mPOST){
            requestPost(callback);
        }else{
            requestGet(callback);
        }
    }

    private void requestPost(NextCallback callback) throws Exception{
        final Request.Builder request = new Request.Builder();
        final Map<String, String> files = new HashMap<>();
        final Map<String, String> raw = new HashMap<>();
        final String query = MapToQuery.toQuery(mParams, true, new MapToQuery.Filter() {
            @Override
            public boolean on(String key, Object value) {
                final String str = String.valueOf(value);
                // check files
                if(str.startsWith(SCHEME_FILE)){
                    files.put(key, str);
                    return false;
                }
                // check raw
                if (key.startsWith(SCHEME_RAW)) {
                    raw.put(key, String.valueOf(value));
                    return false;
                }
                return true;
            }
        });
        // Config
        mRequestFilter.onEachRequest(request);
        final RequestBody body;
        if (!files.isEmpty()){
            // Upload files
            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
            // params
            for (Map.Entry<String, Object> param : mParams.entrySet()) {
                final String keyName = param.getKey();
                if (files.containsKey(keyName)) continue;
                final Headers head = Headers.of("Content-Disposition", String.format("form-data; name=\"%s\"", keyName));
                final RequestBody part = RequestBody.create(MEDIA_TYPE_TEXT, String.valueOf(param.getValue()));
                builder.addPart(head, part);
            }
            // files
            for (Map.Entry<String, String> param : files.entrySet()){
                final String keyName = param.getKey();
                final File file = new File(param.getValue().substring(SCHEME_FILE.length()));
                final String fileName = file.getName();
                final Headers head = Headers.of("Content-Disposition", String.format("form-data; name=\"%s\"; filename=\"%s\"", keyName, fileName));
                final RequestBody part = RequestBody.create(MEDIA_TYPE_JPG, file);
                builder.addPart(head, part);
            }
            body = builder.build();
        } else if(! raw.isEmpty() ){
            // post raw content
            final MediaType type = MediaType.parse(raw.get(SCHEME_RAW + "type"));
            final String content = raw.get(SCHEME_RAW + "content");
            body = RequestBody.create(type, content);
        }else{
            // post query params
            body = RequestBody.create(MEDIA_TYPE_FORM, query);
        }
        request.post(body);
        request.url(mTargetURL);
        Log.d(TAG, "Send POST request, URL= " + mTargetURL + ", Params=" + mParams);
        request(request, callback);
    }

    private void requestGet(NextCallback callback) throws Exception {
        final Request.Builder conf = new Request.Builder();
        String url = mTargetURL;
        if(mParams != null && !mParams.isEmpty()) {
            url += "?" + MapToQuery.toQuery(mParams, true, null);
        }
        Log.d(TAG, "Send GET request, URL= " + url);
        conf.url(url);
        request(conf, callback);
    }

    private void request(Request.Builder conf, NextCallback callback) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        client.setCookieHandler(COOKIES);
        client.setFollowRedirects(false);
        mRequestFilter.onClientCreated(client);
        final Response response = client.newCall(conf.build()).execute();
        callback.onResponse(response.code(), response.body().string());
    }

}
