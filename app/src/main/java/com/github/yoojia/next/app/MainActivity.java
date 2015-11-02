package com.github.yoojia.next.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.github.yoojia.next.clicks.ClickEvent;
import com.github.yoojia.next.clicks.EmitClick;
import com.github.yoojia.next.clicks.NextClickProxy;
import com.github.yoojia.next.events.Event;
import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.events.Subscriber;
import com.github.yoojia.next.flux.Action;
import com.github.yoojia.next.flux.Actions;
import com.github.yoojia.next.flux.Dispatcher;
import com.github.yoojia.next.views.AutoView;
import com.github.yoojia.next.views.NextAutoView;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @AutoView(R.id.helo)
    private TextView mHelo;

    @EmitClick(event = "click")
    @AutoView(R.id.button)
    private Button mButton;

    private final Dispatcher mDispatcher = new Dispatcher();
    private TestStore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EventsFlags.enabledPerformanceLog(true);
//        EventsFlags.enabledProcessingLog(true);

        // Inject views
        NextAutoView.use(this).inject(this);
        // Click proxy
        NextClickProxy.bind(this);
        // Flux
        mStore = new TestStore(mDispatcher, this);
        mStore.register();
        mDispatcher.register(this);

        Subscriber subscriber = new Subscriber() {
            @Override
            public void call(Map<String, Object> values) throws Exception {
                System.out.println(">>>> Subscriber: " + values);
            }
        };

        mDispatcher.subscribe(subscriber, true, Actions.from(TestActions.NOTIFY_CLICK));
    }

    @Subscribe
    private void onClick(@Event("click") ClickEvent<Button> evt) {
        long emitStart = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            long genData = System.currentTimeMillis();
            // Emit action, TestStore will handle this request
            mDispatcher.emit(TestActions.newReqClick(genData));
        }
        long diff = System.currentTimeMillis() - emitStart;
        Log.d(TAG, "- Emit 1000 event, took: " + diff + "ms");
    }

//    @Subscribe
    private void onData(@Event(TestActions.NOTIFY_CLICK) Action evt) {
        final long data = evt.data.getLong("data");
//        Log.d(TAG, "- Received data: " + data);
        mHelo.setText("Received data: " + data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStore.unregister();
        mDispatcher.unregister(this);
        mDispatcher.destroy();
    }
}
