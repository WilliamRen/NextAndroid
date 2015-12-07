package com.github.yoojia.next.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.github.yoojia.next.clicks.ClickEvent;
import com.github.yoojia.next.clicks.ClickEvt;
import com.github.yoojia.next.clicks.NextClickProxy;
import com.github.yoojia.next.events.Evt;
import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.flux.Dispatcher;
import com.github.yoojia.next.views.AutoView;
import com.github.yoojia.next.views.NextAutoView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @AutoView(R.id.helo)
    private TextView mHelo;

    @ClickEvt("click")
    @AutoView(R.id.button)
    private Button mButton;

    private final Dispatcher mDispatcher = new Dispatcher();

    private TestStore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inject views
        NextAutoView.use(this).inject(this);
        // Click proxy
        NextClickProxy.oneshotBind(this);
        // Flux
        mStore = new TestStore(mDispatcher, this);
        mStore.register();
        mDispatcher.register(this);
    }

    @Subscribe
    private void onClick(@Evt("click") ClickEvent<Button> evt) {
        long emitStart = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            long genData = System.currentTimeMillis();
            // Emit action, TestStore will handle this request
            mDispatcher.emit(TestActions.newReqClick(genData));
        }
        long diff = System.currentTimeMillis() - emitStart;
        Log.d(TAG, "- Emit 1000 event, takes: " + diff + "ms");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStore.unregister();
        mDispatcher.unregister(this);
    }
}
