package com.github.yoojia.next.lang;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 0.4
 */
public class CallStack {

    public static String collect(){
        final int startOffset = 3;
        final StringBuilder output = new StringBuilder();
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = startOffset; i < stack.length; i++) {
            final StackTraceElement item = stack[i];
            final String prefix = item.toString();
            if (prefix.startsWith("android.")) {
                break;
            }
            output.append("\n");
            for (int j = 0; j < (i - startOffset); j++) {
                output.append("——");
            }
            output.append(item.toString());
        }
        return output.toString();
    }

}
