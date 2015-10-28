package com.github.yoojia.next.ext.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.yoojia.next.ext.R;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextLabelTextView extends NextLabelStubView {

    private TextView mText;

    public NextLabelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initStub(Context context, AttributeSet attrs) {
        // super.initStub(context, attrs); HOOK Super
        mStub.setLayoutResource(R.layout.next_stub_text);
        mText = (TextView) mStub.inflate();
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NextLabel);
        final CharSequence text = array.getText(R.styleable.NextLabel_android_text);
        if (text != null && text.length() > 0) {
            mText.setHint(text);
        }
        final int textColor = array.getColor(R.styleable.NextLabel_android_textColor, 0);
        if (textColor != 0){
            mText.setTextColor(textColor);
        }
        final float textSize = array.getDimension(R.styleable.NextLabel_android_textSize, 0);
        if (textSize != 0){
            mText.setTextSize(textSize);
        }
        array.recycle();
    }

    public void setText(CharSequence text){
        mText.setText(text);
    }

    public void setText(@StringRes int text){
        mText.setText(text);
    }

    public String getText(){
        return mText.getText().toString();
    }

    public boolean isTextEmpty(){
        return mLabel.getText().length() == 0;
    }
}
