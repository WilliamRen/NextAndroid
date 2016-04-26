package com.github.yoojia.next.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.yoojia.next.R;

/**
 * A simple ActionBar
 * @author  YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public class NextActionbar {

    private final ActionBar mActionBar;
    private final ImageButton mLeftImageButton;
    private final ImageButton mRightImageButton;
    private final Button mLeftTextButton;
    private final Button mRightTextButton;
    private final TextView mTitle;

    private NextActionbar(Activity activity) {
        mActionBar = activity.getActionBar();
        if (mActionBar == null) {
            throw new IllegalArgumentException("Activity["+activity.getClass()+"] has no action bar !");
        }
        mActionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.next_action_bar);
        final View view = mActionBar.getCustomView();
        mLeftImageButton = (ImageButton) view.findViewById(R.id.next_actionbar_left_image);
        mLeftTextButton = (Button) view.findViewById(R.id.next_actionbar_left_text);
        mRightImageButton = (ImageButton) view.findViewById(R.id.next_actionbar_right_image);
        mRightTextButton = (Button) view.findViewById(R.id.next_actionbar_right_text);
        mTitle = (TextView) view.findViewById(R.id.next_actionbar_title);
        mActionBar.show();
    }

    public static NextActionbar use(Activity activity){
        return new NextActionbar(activity);
    }

    public void setBackground(@DrawableRes int resId){
        View view = mActionBar.getCustomView();
        view.setBackgroundResource(resId);
    }

    public void setTitle(@StringRes int resId){
        mTitle.setText(resId);
    }

    public TextView getTitle() {
        return mTitle;
    }

    public String getTitleText() {
        return mTitle.getText().toString();
    }

    public void setLeftImageButton(@DrawableRes int resId, View.OnClickListener listener) {
        mLeftTextButton.setVisibility(View.GONE);
        mLeftImageButton.setVisibility(View.VISIBLE);
        mLeftImageButton.setImageResource(resId);
        mLeftImageButton.setOnClickListener(listener);
    }

    public void setLeftTextButton(@StringRes int resId, View.OnClickListener listener) {
        mLeftImageButton.setVisibility(View.GONE);
        mLeftTextButton.setVisibility(View.VISIBLE);
        mLeftTextButton.setText(resId);
        mLeftTextButton.setOnClickListener(listener);
    }

    public void disableLeftButton(){
        mLeftImageButton.setVisibility(View.GONE);
        mLeftTextButton.setVisibility(View.GONE);
        mLeftImageButton.setOnClickListener(null);
        mLeftTextButton.setOnClickListener(null);
    }

    public void setRightImageButton(@DrawableRes int resId, View.OnClickListener listener) {
        mRightTextButton.setVisibility(View.GONE);
        mRightImageButton.setVisibility(View.VISIBLE);
        mRightImageButton.setImageResource(resId);
        mRightImageButton.setOnClickListener(listener);
    }

    public void setRightTextButton(@StringRes int resId, View.OnClickListener listener) {
        mRightImageButton.setVisibility(View.GONE);
        mRightTextButton.setVisibility(View.VISIBLE);
        mRightTextButton.setText(resId);
        mRightTextButton.setOnClickListener(listener);
    }

    public void disableRightImageButton(){
        mRightImageButton.setVisibility(View.GONE);
        mRightTextButton.setVisibility(View.GONE);
        mRightImageButton.setOnClickListener(null);
        mRightTextButton.setOnClickListener(null);
    }
}
