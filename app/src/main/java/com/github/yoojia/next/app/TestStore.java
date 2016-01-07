package com.github.yoojia.next.app;

import android.util.Log;

import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.flux.Action;
import com.github.yoojia.next.flux.Dispatcher;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class TestStore {

    private final Dispatcher mDispatcher;

    public TestStore(Dispatcher dispatcher) {
        mDispatcher = dispatcher;
    }

    // Run runAsync
    @Subscribe(on = ActionTypes.RAW_MESSAGES)
    public void onMessages(Action act) {
        onLongData((LongMessage) act.message);
        mDispatcher.emit(ActionTypes.createChangedMessages(new LongMessage(System.currentTimeMillis())));
    }

    private void onLongData(LongMessage message) {
        Log.d("TestStore", "Received request, LONG data: " + message.data());
    }

}
