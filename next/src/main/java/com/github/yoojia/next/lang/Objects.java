package com.github.yoojia.next.lang;

import java.lang.reflect.Field;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class Objects {

    private final Object mHost;

    public Objects(Object host) {
        mHost = host;
    }

    public boolean setField(Field field, Object value){
        try {
            field.setAccessible(true);
            field.set(mHost, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int hash(Object obj){
        return obj == null ? 0 : obj.hashCode();
    }
}
