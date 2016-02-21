package com.github.yoojia.next.utils;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextString {

    public static String connect(String root, String path){
        final boolean starts = path.startsWith("/");
        final boolean ends = root.endsWith("/");
        final String uri;
        if(ends && starts){
            uri = root + path.substring(1);
        }else{
            uri = root + (ends || starts ? "" : "/") + path;
        }
        return uri;
    }
}