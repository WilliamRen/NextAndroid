package com.github.yoojia.next.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.github.yoojia.next.clicks.Click;
import com.github.yoojia.next.clicks.ClickEvent;
import com.github.yoojia.next.clicks.NextClickProxy;
import com.github.yoojia.next.events.Runs;
import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.flux.Dispatcher;
import com.github.yoojia.next.views.BindView;
import com.github.yoojia.next.views.NextBindView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.helo)
    private TextView mHelo;

    @Click("click")
    @BindView(R.id.button)
    private Button mButton;

    private final Dispatcher mDispatcher = new Dispatcher();

    private TestStore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inject views
        NextBindView.use(this).inject(this);
        // Click proxy
        NextClickProxy.oneshotBind(this);
        // Flux
        mStore = new TestStore(mDispatcher, this);
        mDispatcher.register(this);
    }

    @Subscribe(on = "click", run = Runs.ON_UI_THREAD)
    private void onClick(ClickEvent<Button> evt) {
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
        mDispatcher.unregister(this);
    }
}
