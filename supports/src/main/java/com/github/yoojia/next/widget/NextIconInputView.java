package com.github.yoojia.next.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.yoojia.next.ext.R;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextIconInputView extends StubDividerView {

    private final EditText mInput;
    private final ImageView mIcon;

    public NextIconInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewStub.setLayoutResource(R.layout.next_icon_input);
        final View view = mViewStub.inflate();
        final Finder finder = Finder.use(view);
        mInput = finder.find(R.id.next_iconinput_input);
        mIcon = finder.find(R.id.next_iconinput_icon);
        // Config
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NextIconInput);
        final CharSequence hint = array.getText(R.styleable.NextIconInput_android_hint);
        if (hint != null && hint.length() > 0) {
            mInput.setHint(hint);
        }
        final int inputType = array.getInt(R.styleable.NextIconInput_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
        mInput.setInputType(inputType);
        final int imeOptions = array.getInt(R.styleable.NextIconInput_android_imeOptions, EditorInfo.IME_ACTION_NEXT);
        mInput.setImeOptions(imeOptions);
        final int image = array.getResourceId(R.styleable.NextIconInput_android_icon, 0);
        if(image != 0) {
            mIcon.setImageResource(image);
        }
        array.recycle();
    }

    public EditText getInput() {
        return mInput;
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public void setText(CharSequence text){
        mInput.setText(text);
    }

    public void setText(@StringRes int text){
        mInput.setText(text);
    }

    public String getText(){
        return mInput.getText().toString();
    }

    public boolean isTextEmpty(){
        return mInput.getText().length() == 0;
    }
}