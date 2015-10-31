package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public final class Actions {

    public static Actions from(String...events) {
        Object[] objects = new Object[events.length * 2];
        for (int i = 0; i < events.length; i++) {
            objects[i * 2] = events[i];
            objects[i * 2 + 1] = Action.class;
        }
        return new Actions(objects);
    }

    private final Object[] mEvents;

    private Actions(Object[] events) {
        mEvents = events;
    }

    Object[] events() {
        return mEvents;
    }
}
