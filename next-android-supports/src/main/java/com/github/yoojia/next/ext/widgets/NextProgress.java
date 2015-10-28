package com.github.yoojia.next.ext.widgets;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.github.yoojia.next.ext.R;

/**
 * a nice progress dialog
 * @author  yoojia.chen@gmail.com
 * @since   1.0
 */
public class NextProgress extends Dialog {

    private TextView mMessage;

    public NextProgress(Context context) {
        super(context, R.style.NextProgressDialog);
        setContentView(R.layout.next_progress);
        setCancelable(false);
        mMessage = (TextView) findViewById(R.id.message);
    }

    public void setMessage(@StringRes int msg){
        mMessage.setText(msg);
    }

    public void setMessage(CharSequence msg){
        mMessage.setText(msg);
    }

    @Override
    public void setTitle(CharSequence title) {
        setMessage(title);
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        setMessage(titleId);
    }
}
