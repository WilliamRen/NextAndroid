package com.github.yoojia.next.ext.system;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public class NextActivityManager implements ActivityLifecycleCallbacks{

    private final Set<Activity> mActivities = new HashSet<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivities.add(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivities.remove(activity);
    }

    public void closeAll(){
        for (Iterator<Activity> iterator = mActivities.iterator();iterator.hasNext();) {
            iterator.next().finish();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

}
