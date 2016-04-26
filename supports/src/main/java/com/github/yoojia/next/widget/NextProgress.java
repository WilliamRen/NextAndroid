package com.github.yoojia.next.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.github.yoojia.next.R;


/**
 * @author  YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since   1.0
 */
public class NextProgress extends Dialog {

    private final Handler mHandler;
    private final TextView mMessage;
    private CharSequence mMessageText;
    private int mMessageId;

    private final Runnable mShowDelayTask = new Runnable() {
        @Override
        public void run() {
            showOnMainThread();
        }
    };

    private final Runnable mHideDelayTask = new Runnable() {
        @Override
        public void run() {
            NextProgress.this.hide();
        }
    };

    private final Runnable mDismissDelayTask = new Runnable() {
        @Override
        public void run() {
            NextProgress.this.dismiss();
        }
    };

    public NextProgress(Context context) {
        super(context, R.style.next_progress);
        setContentView(R.layout.next_progress);
        setCancelable(false);
        mHandler = new Handler();
        mMessage = (TextView) findViewById(R.id.message);
    }

    public NextProgress setMessage(int msg){
        mMessageId = msg;
        return this;
    }

    public NextProgress setMessage(CharSequence msg){
        mMessageText = msg;
        return this;
    }

    @Override
    public void setTitle(CharSequence title) {
        setMessage(title);
    }

    @Override
    public void setTitle(int titleId) {
        setMessage(titleId);
    }

    public void showDelay(long delayMillis) {
        mHandler.postDelayed(mShowDelayTask, delayMillis);
    }

    public void hideDelay(long delayMillis) {
        mHandler.postDelayed(mHideDelayTask, delayMillis);
    }

    public void dismissDelay(long delayMillis) {
        mHandler.postDelayed(mDismissDelayTask, delayMillis);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mShowDelayTask);
        mHandler.removeCallbacks(mHideDelayTask);
        mHandler.removeCallbacks(mDismissDelayTask);
    }

    /**
     * Show方法可以在任意线程中调用
     */
    @Override
    public void show() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showOnMainThread();
        }else{
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showOnMainThread();
                }
            });
        }
    }

    private void showOnMainThread(){
        if (mMessageId != 0) {
            mMessage.setText(mMessageId);
        }else{
            mMessage.setText(mMessageText);
        }
        super.show();
    }

    public static NextProgress create(Context context){
        return new NextProgress(context);
    }

}