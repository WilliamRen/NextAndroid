package com.github.yoojia.next.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-09-14
 */
public class Splitter {

    private final String mSplitterChar;

    private Splitter(String splitterChar) {
        mSplitterChar = splitterChar;
    }

    public List<String> split(String input){
        final List<String> segments = new ArrayList<String>();
        if (input == null || input.isEmpty()) return segments;
        int index = 0, preIndex = index;
        while ((index = input.indexOf(mSplitterChar, index)) != -1){
            if(preIndex != index) {
                segments.add(input.substring(preIndex, index));
            }
            index++;
            preIndex = index;
        }
        if(preIndex < input.length()){
            segments.add(input.substring(preIndex));
        }
        return segments;
    }

    public static Splitter on(char splitterChar){
        return on(String.valueOf(splitterChar));
    }

    public static Splitter on(String splitterChar){
        return new Splitter(splitterChar);
    }
}
