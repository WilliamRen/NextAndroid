package com.github.yoojia.next.app;

import android.util.Log;

import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.flux.Action;
import com.github.yoojia.next.flux.ActionCreator;
import com.github.yoojia.next.flux.ActionTypes;
import com.github.yoojia.next.flux.Dispatcher;
import com.github.yoojia.next.flux.Message;

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
        switch (act.type) {
            case "on-long":
                onLongData((LongMessage) act.message);
                break;
            case "on-string":
                onStringData((StringMessage) act.message);
                break;
        }
        mDispatcher.emit(ActionCreator.createChangedMessage("finish", new LongMessage(System.currentTimeMillis())));
    }

    private void onLongData(LongMessage message) {
        Log.d("TestStore", "Received request, LONG data: " + message.data());
    }

    private void onStringData(StringMessage message) {
        Log.d("TestStore", "Received request, STRING data: " + message.data());
    }
}
