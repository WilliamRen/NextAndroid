package com.github.yoojia.next.inputs;

import android.widget.EditText;
import android.widget.TextView;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public class TextLoader implements ValueLoader<String>{

    private final TextView mInput;

    public TextLoader(TextView input) {
        mInput = input;
    }

    @Override
    public String value0() {
        return mInput.getText().toString();
    }

    public static TextLoader text(TextView textView){
        return new TextLoader(textView);
    }

    public static TextLoader edit(EditText editText) {
        return new TextLoader(editText);
    }
}
