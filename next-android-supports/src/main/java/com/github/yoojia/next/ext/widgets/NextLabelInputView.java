package com.github.yoojia.next.ext.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.github.yoojia.next.ext.R;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextLabelInputView extends NextLabelStubView {

    private EditText mInput;

    public NextLabelInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initStub(Context context, AttributeSet attrs) {
        // super.initStub(context, attrs); HOOK Super
        mStub.setLayoutResource(R.layout.next_stub_input);
        mInput = (EditText) mStub.inflate();
        // Use icon input style
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NextIconInput);
        final CharSequence hint = array.getText(R.styleable.NextIconInput_android_hint);
        if (hint != null && hint.length() > 0) {
            mInput.setHint(hint);
        }
        final int inputType = array.getInt(R.styleable.NextIconInput_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
        mInput.setInputType(inputType);
        final int imeOptions = array.getInt(R.styleable.NextIconInput_android_imeOptions, EditorInfo.IME_ACTION_NEXT);
        mInput.setImeOptions(imeOptions);
        array.recycle();
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
        return mLabel.getText().length() == 0;
    }
}
