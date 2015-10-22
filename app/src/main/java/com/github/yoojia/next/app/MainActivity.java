package com.github.yoojia.next.app;

import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.yoojia.next.views.AutoView;
import com.github.yoojia.next.views.NextAutoView;


public class MainActivity extends AppCompatActivity {

    @AutoView(R.id.helo)
    private TextView mHelo;

    @AutoView(R.id.helo)
    private TextView mHelo1;

    @AutoView(R.id.helo)
    private TextView mHelo2;

    @AutoView(R.id.helo)
    private TextView mHelo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Debug.startMethodTracing();
        NextAutoView.useAndroid(this).inject(this);
        Debug.stopMethodTracing();

        mHelo.setText("HAHAHA");
    }

}
