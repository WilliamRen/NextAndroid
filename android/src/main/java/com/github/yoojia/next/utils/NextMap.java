package com.github.yoojia.next.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-08-16
 */
public class NextMap {

    private Object mPreValue;
    private final Map<String, Object> mSource;

    private NextMap(Map<String, Object> source) {
        this.mSource = source;
        this.mPreValue = source;
    }

    @SuppressWarnings("unchecked")
    public Object get(String keyChain, Object defValue){
        mPreValue = mSource; // reset
        final List<String> keys = split(keyChain, '.');
        final List<String> _keyChain = new ArrayList<>();
        for (String key : keys) {
            _keyChain.add(key);
        }
        final int deep = _keyChain.size();
        for (int i = 0; i < deep; i++) {
            final String keyOnLevel = _keyChain.get(i);
            if(mPreValue == null) {
                break;
            }
            if (Map.class.isAssignableFrom(mPreValue.getClass())){
                final Map<String, Object> map = (Map<String, Object>) mPreValue;
                mPreValue = map.get(keyOnLevel);
            }else{
                if(i < deep) {
                    return defValue;
                }
            }
        }
        return mPreValue == null ? defValue : mPreValue;
    }

    public <T> T getTyped(String keyChain, T defValue){
        return (T) get(keyChain, defValue);
    }

    public String getString(String keyChain, String defValue){
        return getTyped(keyChain, defValue);
    }

    public String getString(String keyChain){
        return (String) getTyped(keyChain, null);
    }

    public static List<String> split(String input, char splitterChar) {
        final List<String> segments = new ArrayList<>();
        if (TextUtils.isEmpty(input)) {
            return segments;
        }else{
            int index = 0;
            int preIndex = index;
            while ((index = input.indexOf(splitterChar, index)) != -1) {
                if (preIndex != index) {
                    segments.add(input.substring(preIndex, index));
                }
                index++;
                preIndex = index;
            }
            if (preIndex < input.length()) {
                segments.add(input.substring(preIndex));
            }
            return segments;
        }
    }

    public static NextMap use(Map<String, Object> source){
        return new NextMap(source);
    }

}