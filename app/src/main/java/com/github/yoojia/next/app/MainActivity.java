package com.github.yoojia.next.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.github.yoojia.next.clicks.Click;
import com.github.yoojia.next.clicks.ClickEvent;
import com.github.yoojia.next.clicks.ClickHandler;
import com.github.yoojia.next.clicks.NextClickProxy;
import com.github.yoojia.next.views.BindView;
import com.github.yoojia.next.views.NextBindView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.helo)
    private TextView mHelo;

    @Click("click")
    @BindView(R.id.button)
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Inject views
        NextBindView.use(this).inject(this);
        // Click proxy
        NextClickProxy.bind(this);

    }

    @ClickHandler(on = "click")
    private void onClick(ClickEvent<Button> evt) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
