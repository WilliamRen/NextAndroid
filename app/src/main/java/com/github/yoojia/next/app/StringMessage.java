package com.github.yoojia.next.app;

import com.github.yoojia.next.flux.Message;

public class StringMessage extends Message {

    public StringMessage(String data) {
        super();
        putString("data", data);
    }

    public String data(){
        return getString("data");
    }
}