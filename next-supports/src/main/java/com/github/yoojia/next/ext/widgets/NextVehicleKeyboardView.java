package com.github.yoojia.next.ext.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.github.yoojia.next.ext.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class NextVehicleKeyboardView extends FrameLayout {

    private final KeyboardAdapter mAdapter;

    private OnItemClickListener mOnItemClickListener;

    public NextVehicleKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.next_vehicle_keyboard, this);
        final Finder finder = Finder.use(this);
        final GridView keyboard = finder.find(R.id.next_vehiclekeyboard_grid);
        mAdapter = new KeyboardAdapter();
        keyboard.setAdapter(mAdapter);
        keyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnItemClickListener != null){
                    final String text = mAdapter.mDataSet.get(position);
                    mOnItemClickListener.onClickItem(text);
                }
            }
        });
    }

    public void update(List<String> keys){
        mAdapter.update(keys);
        mAdapter.notifyDataSetInvalidated();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClickItem(String text);
    }

    private class KeyboardAdapter extends BaseAdapter {

        private final List<String> mDataSet = new ArrayList<>();

        public void update(List<String> data){
            mDataSet.clear();
            mDataSet.addAll(data);
        }

        @Override
        public int getCount() {
            return mDataSet.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.next_vehicle_keyboard_cell, null);
            }
            final TextView button = (TextView) convertView;
            button.setText(mDataSet.get(position));
            return convertView;
        }
    }
}
