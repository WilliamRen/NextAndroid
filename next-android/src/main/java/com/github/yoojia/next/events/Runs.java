package com.github.yoojia.next.events;

import com.github.yoojia.next.events.supports.Schedule;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public enum Runs {
    /**
     * 由调用者线程执行回调
     */
    ON_CALLER(Schedule.FLAG_ON_CALLER),

    /**
     * 由UI主线程执行回调
     */
    ON_UI_THREAD(Schedule.FLAG_ON_UI_THREAD),

    /**
     * 由线程池的线程执行回调
     */
    ON_THREADS(Schedule.FLAG_ON_THREADS);

    final int scheduleFlag;

    Runs(int flag) {
        this.scheduleFlag = flag;
    }
}
