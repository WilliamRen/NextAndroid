package com.github.yoojia.next.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yoojia.next.R;


/**
 * @author  YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since   1.0
 */
public class NextToast {

    private final Toast mToast;
    private final ImageView mIcon;
    private final TextView mMessage;
    private final Resources mRes;

    public NextToast(Context context) {
        mToast = new Toast(context);
        mRes = context.getResources();
        final View view = LayoutInflater.from(context).inflate(R.layout.next_toast, null);
        mToast.setView(view);
        mIcon = (ImageView) view.findViewById(R.id.icon);
        mMessage = (TextView) view.findViewById(R.id.message);
    }

    public void showLong(int iconResId, String message) {
        show(iconResId, message, Toast.LENGTH_LONG);
    }

    public void showLong(String message) {
        showLong(0, message);
    }

    public void showLong(int message) {
        showLong(mRes.getString(message));
    }

    public void show(int iconResId, String message) {
        show(iconResId, message, Toast.LENGTH_SHORT);
    }

    public void show(String message) {
        show(0, message);
    }

    public void show(int message) {
        show(mRes.getString(message));
    }

    private void show(int iconResId, String message, int duration) {
        if (iconResId != 0) {
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setImageResource(iconResId);
        }else{
            mIcon.setVisibility(View.GONE);
        }
        if ( ! TextUtils.isEmpty(message)) {
            mMessage.setText(message);
        }
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

}
