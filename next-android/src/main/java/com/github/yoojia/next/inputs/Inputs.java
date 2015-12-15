package com.github.yoojia.next.inputs;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Inputs Wrapper
 *
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 */
public class Inputs {

    public static TextInput<TextView> textView(TextView textView){
        return new TextInput<>(textView);
    }

    public static TextInput<EditText> editText(EditText editText) {
        return new TextInput<>(editText);
    }

    public static Input radioButton(final RadioButton radioButton) {
        return checkable(radioButton);
    }

    public static Input checkBox(CheckBox checkBox) {
        return checkable(checkBox);
    }

    public static Input toggleButton(ToggleButton toggleButton) {
        return checkable(toggleButton);
    }

    public static Input ratingBar(final RatingBar ratingBar) {
        return new Input() {
            @Override
            public String value() {
                return String.valueOf(ratingBar.getRating());
            }
        };
    }

    public static Input checkable(final CompoundButton checkable) {
        return new Input() {
            @Override
            public String value() {
                return String.valueOf(checkable.isChecked());
            }
        };
    }
}
