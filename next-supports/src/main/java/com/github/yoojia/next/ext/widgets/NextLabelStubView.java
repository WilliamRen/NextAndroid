package com.github.yoojia.next.ext.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.github.yoojia.next.ext.R;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextLabelStubView extends StubDividerView {

    protected final TextView mLabel;
    protected final ViewStub mStub;

    public NextLabelStubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewStub.setLayoutResource(R.layout.next_label_stub);
        final View view = mViewStub.inflate();
        final Finder finder = Finder.use(view);
        mLabel = finder.find(R.id.next_labelstub_label);
        mStub = finder.find(R.id.next_labelstub_stub);
        initLabel(context, attrs);
        initStub(context, attrs);
    }

    protected void initLabel(Context context, AttributeSet attrs){
        final TypedArray textArray = context.obtainStyledAttributes(attrs, R.styleable.NextLabel);
        // 1. label
        final CharSequence label = textArray.getText(R.styleable.NextLabel_labelText);
        if (label != null && label.length() > 0) {
            mLabel.setHint(label);
        }
        final int labelColor = textArray.getColor(R.styleable.NextLabel_labelColor, 0);
        if (labelColor != 0){
            mLabel.setTextColor(labelColor);
        }
        final float labelSize = textArray.getDimension(R.styleable.NextLabel_labelSize, 0);
        if (labelSize != 0){
            mLabel.setTextSize(labelColor);
        }
        final int labelPadding = textArray.getDimensionPixelSize(R.styleable.NextLabel_labelPadding, 0);
        final int labelPaddingLeft = textArray.getDimensionPixelSize(R.styleable.NextLabel_labelPaddingLeft, labelPadding);
        final int labelPaddingTop = textArray.getDimensionPixelSize(R.styleable.NextLabel_labelPaddingTop, labelPadding);
        final int labelPaddingRight = textArray.getDimensionPixelSize(R.styleable.NextLabel_labelPaddingRight, labelPadding);
        final int labelPaddingBottom = textArray.getDimensionPixelSize(R.styleable.NextLabel_labelPaddingBottom, labelPadding);
        mLabel.setPadding(labelPaddingLeft, labelPaddingTop, labelPaddingRight, labelPaddingBottom);
        textArray.recycle();
    }

    protected void initStub(Context context, AttributeSet attrs) {
        final TypedArray stubArray = context.obtainStyledAttributes(attrs, R.styleable.NextLabelStub);
        final int stubLayoutRes = stubArray.getResourceId(R.styleable.NextLabelStub_android_layout, NO_ID);
        if (stubLayoutRes == NO_ID) {
            throw new IllegalArgumentException("layout not set !");
        }
        mStub.setLayoutResource(stubLayoutRes);
        mStub.inflate();
        stubArray.recycle();
    }

    public void setLabel(CharSequence label){
        mLabel.setText(label);
    }

    public void setLabel(@StringRes int label){
        mLabel.setText(label);
    }

    public <T> T findStub(@IdRes int viewId) {
        return (T) findViewById(viewId);
    }
}
