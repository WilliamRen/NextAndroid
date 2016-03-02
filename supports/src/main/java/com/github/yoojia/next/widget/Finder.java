package com.github.yoojia.next.widget;

import android.app.Activity;
import android.view.View;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
class Finder {

    private final View mView;
    private final Activity mActivity;

    private Finder(View root, Activity activity) {
        mView = root;
        mActivity = activity;
        if (root == null && activity == null){
            throw new IllegalArgumentException("Finder require a root view or activity");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T find(int viewId){
        if (mActivity == null) {
            return (T) mView.findViewById(viewId);
        }else{
            return (T) mActivity.findViewById(viewId);
        }
    }

    public static Finder use(View view){
        return new Finder(view, null);
    }

    public static Finder use(Activity activity){
        return new Finder(null, activity);
    }
}