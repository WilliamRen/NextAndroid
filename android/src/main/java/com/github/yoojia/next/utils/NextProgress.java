package com.github.yoojia.next.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.github.yoojia.next.R;


/**
 * @author  yoojia.chen@gmail.com
 * @since   1.0
 */
public class NextProgress extends Dialog {

    private final TextView mMessage;

    public NextProgress(Context context) {
        super(context, R.style.next_progress);
        setContentView(R.layout.next_progress);
        setCancelable(false);
        mMessage = (TextView) findViewById(R.id.message);
    }

    public NextProgress setMessage(int msg){
        mMessage.setText(msg);
        return this;
    }

    public NextProgress setMessage(CharSequence msg){
        mMessage.setText(msg);
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
}