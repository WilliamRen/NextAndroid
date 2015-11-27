package com.github.yoojia.next.inputs;

import android.widget.EditText;
import android.widget.TextView;

/**
 * Input wrapper form TextViews(TextView, EditText, Button ...)
 *
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public final class TextInput<T extends TextView> implements Input{

    final T inputView;

    public TextInput(T input) {
        inputView = input;
    }

    @Override
    public String value() {
        return String.valueOf(inputView.getText());
    }

    public static TextInput<TextView> text(TextView textView){
        return new TextInput<>(textView);
    }

    public static TextInput<EditText> edit(EditText editText) {
        return new TextInput<>(editText);
    }
}
