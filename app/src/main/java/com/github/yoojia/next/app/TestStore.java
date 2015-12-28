package com.github.yoojia.next.app;

import android.app.Activity;

import com.github.yoojia.next.events.Runs;
import com.github.yoojia.next.events.Subscribe;
import com.github.yoojia.next.flux.AbstractStore;
import com.github.yoojia.next.flux.Action;
import com.github.yoojia.next.flux.Dispatcher;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class TestStore extends AbstractStore<Activity> {

    protected TestStore(Dispatcher dispatcher, Activity contextHost) {
        super(dispatcher, contextHost);
    }

    // Run runAsync
    @Subscribe(on = TestActions.REQ_CLICK, run = Runs.ON_THREADS)
    private void onClick(Action evt) {
        final long data = evt.data.getLong("data");
//        Log.d("TestStore", "Received request, data: " + data);
        // Emit result, invoke View to update
        dispatch(TestActions.newNotifyClick(data));
    }
}
