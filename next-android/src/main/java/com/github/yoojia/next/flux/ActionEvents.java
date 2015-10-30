package com.github.yoojia.next.flux;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class ActionEvents {

    private final Object[] mEvents;

    private ActionEvents(Object[] mEvents) {
        this.mEvents = mEvents;
    }

    public static ActionEvents events(String...events) {
        final Object[] params = new Object[events.length * 2];
        for (int i = 0; i < events.length; i++) {
            final int index = i * 2;
            params[index] = events[i];
            params[index + 1] = Action.class;
        }
        return new ActionEvents(params);
    }

    Object[] events() {
        return mEvents;
    }
}
