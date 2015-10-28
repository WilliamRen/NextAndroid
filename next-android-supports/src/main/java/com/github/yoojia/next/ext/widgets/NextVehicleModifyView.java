package com.github.yoojia.next.ext.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.yoojia.next.ext.R;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextVehicleModifyView extends FrameLayout{

    private final RadioButton mText01;
    private final RadioButton mText02;
    private final RadioButton mText03;
    private final RadioButton mText04;
    private final RadioButton mText05;
    private final RadioButton mText06;
    private final RadioButton mText07;
    private final RadioGroup mRadioGroup;
    private final RadioButton[] mViews;

    private OnItemSelectedListener mOnItemSelectedListener;

    private RadioButton mCheckedButton;

    public NextVehicleModifyView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.next_vehicle_modify, this);
        // Find views
        final Finder finder = Finder.use(this);
        mRadioGroup = finder.find(R.id.next_vehiclemodify_group);
        mText01 = finder.find(R.id.next_vehiclemodify_text_1);
        mText02 = finder.find(R.id.next_vehiclemodify_text_2);
        mText03 = finder.find(R.id.next_vehiclemodify_text_3);
        mText04 = finder.find(R.id.next_vehiclemodify_text_4);
        mText05 = finder.find(R.id.next_vehiclemodify_text_5);
        mText06 = finder.find(R.id.next_vehiclemodify_text_6);
        mText07 = finder.find(R.id.next_vehiclemodify_text_7);
        // Bind events
        mViews = new RadioButton[]{mText01, mText02, mText03, mText04, mText05, mText06, mText07};
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mOnItemSelectedListener != null) {
                    mCheckedButton = finder.find(checkedId);
                    final int index = group.indexOfChild(mCheckedButton);
                    mOnItemSelectedListener.onSelectedItem(index, mCheckedButton);
                }
            }
        });
    }

    public void setCheckedButton(String text){
        if (text == null || text.isEmpty()) {
            return;
        }
        setCheckedButton(text.charAt(0));
    }

    public void setCheckedButton(char text){
        if (mCheckedButton == null) {
            return;
        }
        mCheckedButton.setText(String.valueOf(text));
    }

    public boolean selectNext(){
        final int index = mRadioGroup.indexOfChild(mCheckedButton) + 1;
        if(index >= mViews.length){
            return false;
        }
        mViews[index].performClick();
        return true;
    }

    public void setText(String number){
        if (number == null || number.isEmpty()) {
            return;
        }
        final int size = number.length();
        if (6 > size || size > 7){
            throw new IllegalArgumentException("Illegal length of Vehicle number: " + number);
        }
        if (size == 6) {
            number = " " + number;
        }
        final char[] chars = number.toCharArray();
        for (int i = 0; i < 7; i++){
            mViews[i].setText(String.valueOf(chars[i]));
        }
    }

    public String getText(){
        StringBuilder out = new StringBuilder(7);
        for (TextView v : mViews){
            out.append(v.getText());
        }
        return out.toString();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onSelectedItem(int index, RadioButton target);
    }

}
