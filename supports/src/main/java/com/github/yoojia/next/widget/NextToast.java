package com.github.yoojia.next.widget;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
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

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Style mStyle;

    public NextToast(Context context, Style style) {
        mStyle = style;
        mToast = new Toast(context);
        mRes = context.getResources();
        final View view = LayoutInflater.from(context).inflate(R.layout.next_toast, null);
        mToast.setView(view);
        mIcon = (ImageView) view.findViewById(R.id.icon);
        mMessage = (TextView) view.findViewById(R.id.message);
    }

    /**
     * 设置提示样式
     * @param style 样式
     */
    public void setStyle(Style style) {
        mStyle = style;
    }

    /**
     * 指定图标资源ID，显示长时间的提示信息
     * @param iconResId 图标资源ID
     * @param message 提示信息内容
     */
    public void showLong(@DrawableRes int iconResId, String message) {
        show(iconResId, message, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时间的提示信息
     * @param message 提示信息内容
     */
    public void showLong(String message) {
        showLong(mStyle.resId, message);
    }

    /**
     * 显示长时间的提示信息
     * @param message 提示信息内容
     */
    public void showLong(@StringRes int message) {
        showLong(mRes.getString(message));
    }

    /**
     * 指定图标资源ID，显示短时间的提示信息
     * @param iconResId 图标资源ID
     * @param message 提示信息内容
     */
    public void show(@DrawableRes int iconResId, String message) {
        show(iconResId, message, Toast.LENGTH_SHORT);
    }

    /**
     * 使用创建NextToast指定类型的图标资源ID，显示短时间的提示信息
     * @param message 提示信息内容
     */
    public void show(String message) {
        show(mStyle.resId, message);
    }

    /**
     * 使用创建NextToast指定类型的图标资源ID，显示短时间的提示信息
     * @param message 提示信息内容
     */
    public void show(@StringRes int message) {
        show(mRes.getString(message));
    }

    /**
     * 创建一个无图标的NextToast
     * @param context Context
     * @return NextToast
     */
    public static NextToast create(Context context){
        return new NextToast(context, Style.None);
    }

    /**
     * 创建一个对号图标的NextToast
     * @param context Context
     * @return NextToast
     */
    public static NextToast success(Context context){
        return new NextToast(context, Style.SUCCESS);
    }

    /**
     * 创建一个叉号图标的NextToast
     * @param context Context
     * @return NextToast
     */
    public static NextToast fail(Context context){
        return new NextToast(context, Style.FAIL);
    }

    /**
     * 创建一个叹号图标的NextToast
     * @param context Context
     * @return NextToast
     */
    public static NextToast warn(Context context){
        return new NextToast(context, Style.WARN);
    }

    private void show(@DrawableRes final int iconResId, final String message, final int duration) {
        final Runnable task = new Runnable() {
            @Override public void run() {
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
        };
        if (Looper.getMainLooper() == Looper.myLooper()) {
            task.run();
        }else{
            mMainHandler.post(task);
        }
    }

    public enum Style{
        None(0),
        SUCCESS(R.drawable.next_icon_success),
        FAIL(R.drawable.next_icon_fail),
        WARN(R.drawable.next_icon_warning)
        ;
        private final int resId;

        Style(int resId) {
            this.resId = resId;
        }
    }

}
