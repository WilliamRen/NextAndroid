package com.github.yoojia.next.inputs;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Default message display
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
class DefaultMessageDisplay implements MessageDisplay {

    private final static String TAG = DefaultMessageDisplay.class.getSimpleName();

    private View mInputView;

    public void attach(Input input){
        if (input instanceof TextInput) {
            mInputView = ((TextInput) input).inputView;
        }else{
            Log.e(TAG, "- When use <DefaultMessageDisplay>, <TextInput> is recommend !");
            mInputView = null;
        }
    }

    @Override
    public void show(String message) {
        if (mInputView == null) {
            Log.w(TAG, "- TestResult.message=" + message);
            return;
        }
        if (TextView.class.isAssignableFrom(mInputView.getClass())) {
            final TextView text = (TextView) mInputView;
            text.setError(message);
        }else{
            Toast.makeText(mInputView.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
