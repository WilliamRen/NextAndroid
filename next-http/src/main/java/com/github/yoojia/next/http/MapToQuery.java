package com.github.yoojia.next.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
class MapToQuery {

    public static String toQuery(Map<String, Object> params, boolean encoded, Filter filter) throws UnsupportedEncodingException {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> item : params.entrySet()) {
            final String key = item.getKey();
            final Object value = item.getValue();
            if (filter != null && !filter.on(key, value)) {
                continue;
            }
            builder.append(key);
            builder.append("=");
            if (encoded) {
                builder.append(URLEncoder.encode(String.valueOf(value), "UTF-8"));
            }else{
                builder.append(value);
            }
            builder.append("&");
        }
        final int len = builder.length();
        if (len > 2) {
            builder.delete(len - 1 , len);
        }
        return builder.toString();
    }

    public interface Filter {
        boolean on(String key, Object value);
    }
}
