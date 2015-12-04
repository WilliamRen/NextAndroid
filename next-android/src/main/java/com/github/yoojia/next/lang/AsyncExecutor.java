package com.github.yoojia.next.lang;

import android.os.AsyncTask;
import android.os.Build;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public final class AsyncExecutor extends AsyncTask<Runnable, Void, Void>{

    @Override
    protected Void doInBackground(Runnable... params) {
        final Runnable runnable = params[0];
        runnable.run();
        return null;
    }

    public static void exe(Runnable runnable) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            new AsyncExecutor().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, runnable);
        } else {
            new AsyncExecutor().execute(new Runnable[]{runnable});
        }
    }
}
