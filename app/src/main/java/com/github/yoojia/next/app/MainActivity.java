package com.github.yoojia.next.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.yoojia.next.lang.Objects;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(Objects.findAndroidParent(Type2.class));
    }

    public class Type1 extends Activity{}

    public class Type2 extends Type1{}
}
