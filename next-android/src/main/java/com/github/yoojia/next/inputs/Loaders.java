package com.github.yoojia.next.inputs;

import android.widget.EditText;
import android.widget.TextView;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public class Loaders implements Loader<String> {

    private final TextView mInput;

    public Loaders(TextView input) {
        mInput = input;
    }

    @Override
    public String onLoadValue() {
        return mInput.getText().toString();
    }

    public static Loaders textView(TextView textView){
        return new Loaders(textView);
    }

    public static Loaders editText(EditText editText) {
        return textView(editText);
    }
}
