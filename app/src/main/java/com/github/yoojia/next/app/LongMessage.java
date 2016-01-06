package com.github.yoojia.next.app;

import com.github.yoojia.next.flux.Message;

public class LongMessage extends Message {

    public LongMessage(long data) {
        super();
        putLong("data", data);
    }

    public long data(){
        return getLong("data");
    }
}