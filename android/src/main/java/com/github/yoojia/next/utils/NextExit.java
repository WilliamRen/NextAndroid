package com.github.yoojia.next.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-08-16
 */
public class NextExit {

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

    public NextExit(Activity context, String confirmExitMsg) {
        mContext = context;
        mConfirmExitMsg = confirmExitMsg;
    }

    public NextExit(Activity context, int confirmExitMsgResId) {
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