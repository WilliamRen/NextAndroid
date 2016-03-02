package com.github.yoojia.next.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.github.yoojia.next.ext.R;

/**
 * @author  YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since   1.0
 */
public class NextProgress extends Dialog {

    private final TextView mMessage;
    private CharSequence mMessageText;
    private int mMessageId;

    private Handler mHandler;

    public NextProgress(Context context) {
        super(context, R.style.next_progress);
        setContentView(R.layout.next_progress);
        setCancelable(false);
        mMessage = (TextView) findViewById(R.id.message);
    }

    public NextProgress setMessage(@StringRes int msg){
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
    public void setTitle(@StringRes int titleId) {
        setMessage(titleId);
    }

    /**
     * Show方法可以在任意线程中调用
     */
    @Override
    public void show() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            finallyShow();
        }else{
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    finallyShow();
                }
            });
        }
    }

    private void finallyShow(){
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