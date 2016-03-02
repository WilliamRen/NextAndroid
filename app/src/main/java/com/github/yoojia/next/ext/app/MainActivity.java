package com.github.yoojia.next.ext.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.yoojia.next.widget.NextToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        new NextProgress(this).setMessage("这是一条很长很长的消息").show();
        final NextToast toast = new NextToast(MainActivity.this, NextToast.Style.FAIL);
        new Thread(new Runnable() {
            @Override
            public void run() {
                toast.showLong("这是一条很长很长的消息");
            }
        }).start();

    }
}
