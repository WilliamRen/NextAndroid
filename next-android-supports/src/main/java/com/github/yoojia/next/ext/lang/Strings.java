package com.github.yoojia.next.ext.lang;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public class Strings {

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
