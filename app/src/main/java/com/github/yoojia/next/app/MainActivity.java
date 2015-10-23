package com.github.yoojia.next.app;

import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.github.yoojia.next.clicks.ClickEvent;
import com.github.yoojia.next.clicks.EmitClick;
import com.github.yoojia.next.clicks.NextClickProxy;
import com.github.yoojia.next.events.Event;
import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.flux.Action;
import com.github.yoojia.next.flux.Dispatcher;
import com.github.yoojia.next.views.AutoView;
import com.github.yoojia.next.views.NextAutoView;


public class MainActivity extends AppCompatActivity {

    @AutoView(R.id.helo)
    private TextView mHelo;

    @EmitClick(event = "click")
    @AutoView(R.id.button)
    private Button mButton;

    private final Dispatcher mDispatcher = new Dispatcher(AppCompatActivity.class);
    private TestStore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Debug.startMethodTracing();
        // Inject views
        NextAutoView.useAndroid(this).inject(this);
        // Click proxy
        NextClickProxy.bindAndroid(this);
        // Flux
        mStore = new TestStore(mDispatcher, this);
        mStore.register();
        // mDispatcher.register(this);
        mDispatcher.registerAsync(this);

    }

    @Subscribe
    private void onClick(@Event("click") ClickEvent<Button> evt) {
        long genData = System.currentTimeMillis();
        // Emit action, TestStore will handle this request
        mDispatcher.emit(TestActions.newReqClick(genData));
    }

    @Subscribe
    private void onData(@Event(TestActions.NOTIFY_CLICK) Action evt) {
        final long data = evt.data.getLong("data");
        mHelo.setText("Received data: " + data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStore.unregister();
        mDispatcher.unregister(this);
        mDispatcher.shutdown();
        //
        Debug.stopMethodTracing();
    }
}
