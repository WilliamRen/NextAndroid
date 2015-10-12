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
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public class Requests {

    private static final String TAG = "REQUESTS";

    private static final String SCHEME_FILE = "file://";
    private static final String SCHEME_RAW = "REQUEST#RAW-POST://";

    private static final MediaType MEDIA_TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("plain/text; charset=utf-8");

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    private static final CookieManager COOKIES = new CookieManager();
    private static final ExecutorService THREADS = Executors.newCachedThreadPool();

    private final String mBaseURL;
    private final Map<String, Object> mParams = new HashMap<>();

    private boolean mPOST = false;
    private String mTargetURL;

    public Requests(String baseURL) {
        mBaseURL = baseURL;
        mTargetURL = baseURL;
    }

    public Requests post(String uri) {
        return post(uri, Collections.EMPTY_MAP);
    }

    public Requests post(String uri, String jsonText){
        return post(uri, jsonText, "application/json; charset=utf-8");
    }

    public Requests post(String uri, String content, String contentType){
        final Map<String, Object> params = new HashMap<>(2);
        params.put(SCHEME_RAW + "content", content);
        params.put(SCHEME_RAW + "type", contentType);
        return post(uri, params);
    }

    public Requests post(String uri, Map<String, Object> params){
        mTargetURL = mBaseURL + uri;
        return post(params);
    }

    public Requests post(Map<String, Object> params) {
        mPOST = true;
        mParams.clear();
        if (params != null && !params.isEmpty()){
            mParams.putAll(params);
        }
        return this;
    }

    public Requests get(String uri) {
        return get(uri, Collections.EMPTY_MAP);
    }

    public Requests get(String uri, Map<String, Object> params){
        mTargetURL = mBaseURL + uri;
        return get(params);
    }

    public Requests get(Map<String, Object> params) {
        mPOST = false;
        mParams.clear();
        if (params != null && !params.isEmpty()){
            mParams.putAll(params);
        }
        return this;
    }

    public void go(final Callback callback){
        THREADS.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendRequest(callback);
                } catch (Exception errors) {
                    callback.onErrors(errors);
                } finally {
                    callback.onEnd();
                }
            }
        });
    }

    private void sendRequest(final Callback callback) throws Exception{
        callback.onStart();
        if (mPOST){
            requestPost(callback);
        }else{
            requestGet(callback);
        }
    }

    private void requestPost(Callback callback) throws Exception{
        final Request.Builder conf = new Request.Builder();
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
        conf.post(body);
        conf.url(mTargetURL);
        Log.d(TAG, "Send POST request, URL= " + mTargetURL + ", Params=" + mParams);
        request(conf, callback);
    }

    private void requestGet(Callback callback) throws Exception {
        final Request.Builder conf = new Request.Builder();
        String url = mTargetURL;
        if(mParams != null && !mParams.isEmpty()) {
            url += "?" + MapToQuery.toQuery(mParams, true, null);
        }
        Log.d(TAG, "Send GET request, URL= " + url);
        conf.url(url);
        request(conf, callback);
    }

    private void request(Request.Builder conf, Callback callback) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        client.setCookieHandler(COOKIES);
        client.setFollowRedirects(false);
        final Response response = client.newCall(conf.build()).execute();
        callback.onResponse(response.code(), response.body().string());
    }
}
