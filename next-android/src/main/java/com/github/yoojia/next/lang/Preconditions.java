package com.github.yoojia.next.lang;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class Preconditions {

    public static void notNull(Object target, String message) {
        if (target == null) {
            throw new NullPointerException(message);
        }
    }

    public static void notNull(Object target) {
        if (target == null) {
            throw new NullPointerException();
        }
    }

    public static void notEmpty(CharSequence value, String message) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
