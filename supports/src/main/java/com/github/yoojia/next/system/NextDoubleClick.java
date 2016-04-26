package com.github.yoojia.next.system;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 2.1
 */
public class NextDoubleClick {

    private final AtomicBoolean mWaitingSecond = new AtomicBoolean(false);
    private final Activity mContext;
    private final String mMessage;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mResetWaiting = new Runnable() {
        @Override
        public void run() {
            mWaitingSecond.set(false);
        }
    };

    private long mDoubleClickEscape = 2500;
    private OnDoubleClickListener mOnDoubleClickListener;

    public NextDoubleClick(Activity context, String message) {
        mContext = context;
        mMessage = message;
    }

    public NextDoubleClick(Activity context, @StringRes int confirmExitMsgResId) {
        this(context, context.getResources().getString(confirmExitMsgResId));
    }

    /**
     * 接入Activity的onKeyDown方法
     * @param keyCode keyCode
     * @param event event
     */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK ) {
            if ( ! mWaitingSecond.get()) {
                mWaitingSecond.set(true);
                Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(mResetWaiting, mDoubleClickEscape);
                return true;
            }else{
                if (mOnDoubleClickListener != null) {
                    mOnDoubleClickListener.onDoubleClick();
                }
                return false;
            }
        }else{
            return false; // Not back click
        }
    }

    public void setDoubleClickEscape(long doubleClickEscape) {
        mDoubleClickEscape = doubleClickEscape;
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }

    public interface OnDoubleClickListener {
        void onDoubleClick();
    }
}