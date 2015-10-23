package com.github.yoojia.next.app;

import android.app.Activity;
import android.util.Log;

import com.github.yoojia.next.events.Event;
import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.flux.Action;
import com.github.yoojia.next.flux.Dispatcher;
import com.github.yoojia.next.flux.Store;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class TestStore extends Store<Activity>{

    protected TestStore(Dispatcher dispatcher, Activity contextHost) {
        super(dispatcher, contextHost);
    }

    // Run on async
    @Subscribe(async = true)
    private void onClick(@Event(TestActions.REQ_CLICK) Action evt) {
        final long data = evt.data.getLong("data");
        Log.d("TestStore", "Received request, data: " + data);
        // Emit result, notify View to update
        emit(TestActions.newNotifyClick(data));
    }
}
