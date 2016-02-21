package com.github.yoojia.next.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

    public void to(Class<? extends Activity> target, Bundle params) {
        to(target, params, null);
    }

    public void to(Class<? extends Activity> target, Bundle params, Filter filter) {
        if(params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Bundle params MUST not be null !");
        }
        final Intent intent = new Intent(mContext, target);
        intent.putExtras(params);
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