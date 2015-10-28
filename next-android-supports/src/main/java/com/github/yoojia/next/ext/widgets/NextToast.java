package com.github.yoojia.next.ext.widgets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yoojia.next.ext.R;

import java.lang.ref.WeakReference;

/**
 * A nice toast
 *
 * @author  yoojia.chen@gmail.com
 * @version 2015-04-09
 * @since   1.0
 */
public class NextToast {

    private static WeakReference<Toast> PRE_TOAST;
    private final Toast toast;
    private final TextView message;

    public enum Style {

        OK(R.drawable.next_toast_bg_green),
        Warning(R.drawable.next_toast_bg_red),
        Confirm(R.drawable.next_toast_bg_blue),
        Info(R.drawable.next_toast_bg_gray);

        private final int drawable;

        Style(int drawable) {
            this.drawable = drawable;
        }
    }

    public NextToast(Context context, Style style) {
        final View view = LayoutInflater.from(context).inflate(R.layout.next_toast, null);
        view.setBackgroundResource(style.drawable);
        view.setPadding(35, 35, 35, 35);
        toast = new Toast(context);
        message = (TextView) view.findViewById(R.id.tip);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER,0,0);
        PRE_TOAST = new WeakReference<>(toast);
    }

    public void show(@StringRes int message){
        showShort(message);
    }

    public void show(String message){
        showShot(message);
    }

    public void showShort(@StringRes int message){
        show(message, Toast.LENGTH_SHORT);
    }

    public void showShot(String message){
        show(message, Toast.LENGTH_SHORT);
    }

    public void showLong(@StringRes int message){
        show(message, Toast.LENGTH_LONG);
    }

    public void showLong(String message){
        show(message, Toast.LENGTH_LONG);
    }

    private void show(int message, int duration) {
        cancelPre();
        this.message.setText(message);
        toast.setDuration(duration);
        toast.show();
    }

    private void show(String message, int duration) {
        cancelPre();
        this.message.setText(message);
        toast.setDuration(duration);
        toast.show();
    }

    public static NextToast make(Context context){
        return make(context, Style.Info);
    }

    public static NextToast make(Context context, Style style){
        return new NextToast(context, style);
    }

    private void cancelPre(){
        if (PRE_TOAST != null){
            Toast cached = PRE_TOAST.get();
            if (cached != null && cached != this.toast) cached.cancel();
        }
    }
}