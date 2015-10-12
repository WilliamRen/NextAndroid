package com.github.yoojia.next.ext.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.github.yoojia.next.lang.Primitives;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextActivities {

    private final Context mContext;

    private NextActivities(Context context) {
        mContext = context;
    }

    public void to(Class<? extends Activity> target){
        final Intent intent = new Intent(mContext, target);
        mContext.startActivity(intent);
    }

    public void to(Class<? extends Activity> target, Filter filter){
        final Intent intent = new Intent(mContext, target);
        if (filter != null) {
            filter.on(intent);
        }
        mContext.startActivity(intent);
    }

    public void to(Class<? extends Activity> target, Map<String, Object> params) {
        to(target, params, null);
    }

    public void to(Class<? extends Activity> target, Map<String, Object> params, Filter filter) {
        if(params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Params MUST not be null !");
        }
        final Intent intent = new Intent(mContext, target);
        for (Map.Entry<String, Object> entry : params.entrySet()){
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (value == null) continue;
            if (value instanceof Serializable){
                intent.putExtra(key, (Serializable)value);
                continue;
            }
            // primitive types:
            final Class<?> type = value.getClass();
            final Class<?> wrapType = Primitives.getWrapClass(type);
            if (String.class.equals(wrapType)) {
                intent.putExtra(key, (String)value);
            }else if (Integer.class.equals(wrapType)) {
                intent.putExtra(key, (Integer)value);
            }else if (Long.class.equals(wrapType)) {
                intent.putExtra(key, (Long)value);
            }else if (Float.class.equals(wrapType)) {
                intent.putExtra(key, (Float)value);
            }else if (Double.class.equals(wrapType)) {
                intent.putExtra(key, (Double)value);
            }else if (Boolean.class.equals(wrapType)) {
                intent.putExtra(key, (Boolean)value);
            }else if (Byte.class.equals(wrapType)) {
                intent.putExtra(key, (Byte)value);
            }else if (Short.class.equals(wrapType)) {
                intent.putExtra(key, (Short)value);
            }
        }
        if (filter != null) {
            filter.on(intent);
        }
        mContext.startActivity(intent);
    }

    public static NextActivities use(Context context){
        return new NextActivities(context);
    }

    public interface Filter {
        void on(Intent intent);
    }
}
