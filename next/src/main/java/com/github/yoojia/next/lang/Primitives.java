package com.github.yoojia.next.lang;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 1.0
 */
public class Primitives {

    public static Class<?> getWrapClass(Class<?> type){
        if(byte.class.equals(type) || Byte.class.equals(type)) return Byte.class;
        else if(short.class.equals(type) || Short.class.equals(type)) return Short.class;
        else if(int.class.equals(type) || Integer.class.equals(type)) return Integer.class;
        else if(long.class.equals(type) || Long.class.equals(type)) return Long.class;
        else if(float.class.equals(type) || Float.class.equals(type)) return Float.class;
        else if(double.class.equals(type) || Double.class.equals(type)) return Double.class;
        else if(char.class.equals(type) || Character.class.equals(type)) return Character.class;
        else if(boolean.class.equals(type) || Boolean.class.equals(type)) return Boolean.class;
        else if(String.class.equals(type)) return String.class;
        else throw new IllegalArgumentException("Not a primitive type: " + type);
    }
}
