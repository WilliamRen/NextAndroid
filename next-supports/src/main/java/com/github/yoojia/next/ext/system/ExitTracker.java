package com.github.yoojia.next.ext.system;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExitTracker {

    private final AtomicBoolean mExitFlag = new AtomicBoolean(false);
    private final Activity mContext;
    private final String mConfirmExitMsg;
    private final Handler mResetExitFlagHandler = new Handler(Looper.getMainLooper());
    private final Runnable mResetExitFlagTask = new Runnable() {
        @Override
        public void run() {
            mExitFlag.set(false);
        }
    };

    public ExitTracker(Activity context, String confirmExitMsg) {
        mContext = context;
        mConfirmExitMsg = confirmExitMsg;
    }

    public ExitTracker(Activity context, int confirmExitMsgResId) {
        this(context, context.getResources().getString(confirmExitMsgResId));
    }

    /**
     * 接入Activity的onKeyDown方法
     * @param keyCode keyCode
     * @param event event
     */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && !mExitFlag.get()) {
            mExitFlag.set(true);
            Toast.makeText(mContext, mConfirmExitMsg, Toast.LENGTH_SHORT).show();
            mResetExitFlagHandler.postDelayed(mResetExitFlagTask, 2500);
            return true;
        }else{
            return false;
        }
    }
}