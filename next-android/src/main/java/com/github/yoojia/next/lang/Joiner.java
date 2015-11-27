package com.github.yoojia.next.lang;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-09-14
 */
public class Joiner {

    private final String mJoinerChar;

    private Joiner(String joinerChar) {
        mJoinerChar = joinerChar;
    }

    public String join(Iterable<?> source){
        if (source == null) {
            return null;
        }
        final Iterator<?> iterator = source.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(iterator.next());
        while (iterator.hasNext()){
            final Object value = iterator.next();
            if (value == null) {
                continue;
            }
            buf.append(mJoinerChar);
            buf.append(value);
        }
        return buf.toString();
    }

    public String join(Object[] source) {
        return join(Arrays.asList(source));
    }

    public static Joiner on(String joinerChar){
        return new Joiner(joinerChar);
    }

    public static Joiner on(char joinerChar){
        return on(String.valueOf(joinerChar));
    }
}
