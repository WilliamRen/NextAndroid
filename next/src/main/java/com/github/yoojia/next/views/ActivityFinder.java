package com.github.yoojia.next.views;

import android.app.Activity;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class ActivityFinder extends ViewFinder {

    public ActivityFinder(Activity activity) {
        super(activity.getWindow().getDecorView());
    }
}
