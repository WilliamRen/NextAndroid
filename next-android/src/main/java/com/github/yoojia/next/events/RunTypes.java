package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Schedule;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public enum RunTypes {
    /**
     * 由调用者线程执行回调
     */
    CALLER(Schedule.FLAG_ON_CALLER),

    /**
     * 由主线程执行回调
     */
    MAIN_THREAD(Schedule.FLAG_ON_MAIN_THREAD),

    /**
     * 由线程池的线程执行回调
     */
    THREADS(Schedule.FLAG_ON_THREADS);

    final int scheduleFlag;

    RunTypes(int flag) {
        this.scheduleFlag = flag;
    }
}
