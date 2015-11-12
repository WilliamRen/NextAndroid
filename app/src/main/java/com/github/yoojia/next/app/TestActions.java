package com.github.yoojia.next.app;

import com.github.yoojia.next.flux.Action;
import com.github.yoojia.next.flux.Action.Builder;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class TestActions {

    public static final String REQ_CLICK = "req-click";

    public static final String NOTIFY_CLICK = "notify-click";

    public static Action newReqClick(long data) {
        return new Builder()
                .setType(REQ_CLICK)
                .putLong("data", data)
                .build();
    }

    public static Action newNotifyClick(long data) {
        return new Builder()
                .setType(NOTIFY_CLICK)
                .putLong("data", data)
                .build();
    }
}
