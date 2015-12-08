package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Schedule;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 */
public enum RunOn {
    CALLER(Schedule.FLAG_ON_CALLER),
    MAIN(Schedule.FLAG_ON_MAIN),
    THREADS(Schedule.FLAG_ON_THREADS);

    final int scheduleFlag;

    RunOn(int flag) {
        this.scheduleFlag = flag;
    }
}
