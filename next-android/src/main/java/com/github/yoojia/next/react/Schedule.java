package com.github.yoojia.next.react;

import java.util.concurrent.Callable;

/**
 * 任务调度器
 * @author YOOJIA.CHEN (yoojiachen@gmail.com)
 */
public interface Schedule {

    int FLAG_ON_MAIN =      1 << 1;
    int FLAG_ON_THREADS =   1 << 2;
    int FLAG_ON_CALLER =    1 << 3;

    /**
     * 根据给定的Flags来决定如何调度任务的执行
     * @param task 需要被调度执行的任务
     * @param flags 指定执行的Flags
     * @throws ScheduleException 任务执行过程时抛出异常
     */
    void submit(Callable<Void> task, int flags) throws ScheduleException;

}
