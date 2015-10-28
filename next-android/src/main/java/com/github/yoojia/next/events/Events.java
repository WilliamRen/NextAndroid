package com.github.yoojia.next.events;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
final public class Events {

    public static Object[] on1(String event, Class<?> type) {
        return new Object[]{event, type};
    }

    public static Object[] on2(String event1, Class<?> type1, String event2, Class<?> type2) {
        return new Object[]{event1, type1, event2, type2};
    }

    public static Object[] on3(String event1, Class<?> type1, String event2, Class<?> type2,
                               String event3, Class<?> type3) {
        return new Object[]{event1, type1, event2, type2, event3, type3};
    }

    public static Object[] on4(String event1, Class<?> type1, String event2, Class<?> type2,
                               String event3, Class<?> type3, String event4, Class<?> type4) {
        return new Object[]{event1, type1, event2, type2, event3, type3, event4, type4};
    }

    public static Object[] on5(String event1, Class<?> type1, String event2, Class<?> type2,
                               String event3, Class<?> type3, String event4, Class<?> type4,
                               String event5, Class<?> type5) {
        return new Object[]{event1, type1, event2, type2, event3, type3, event4, type4, event5, type5};
    }
}
