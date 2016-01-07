package com.github.yoojia.next.flux;

import com.github.yoojia.next.events.Meta;
import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.lang.CallStack;
import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.events.supports.OnTargetMissListener;
import com.github.yoojia.next.events.supports.Schedule;
import com.github.yoojia.next.events.supports.Schedules;

import java.lang.reflect.Method;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Dispatcher {

    private static final String CALL_STACK_WARN = "Use <dispatcher.setTraceEnabled(...)> to collect methods stack !";

    private final NextEvents mEvents;

    private boolean mTraceEnabled = false;

    public Dispatcher(Schedule schedulers) {
        mEvents = new NextEvents(schedulers);
        setOnTargetMissListener(null); // Set NULL to allow miss target
    }

    public Dispatcher(){
        this(Schedules.sharedThreads());
    }

    /**
     * 扫描目标并注册其@Subscribe方法
     * @param host 目标对象实例
     */
    public void register(Object host){
        mEvents.register(host, new Filter<Method>() {
            // Only accept <Action> type
            @Override
            public boolean accept(Method method) {
                final Class<?>[] types = method.getParameterTypes();
                return Action.class.equals(types[0]);
            }
        });
    }

    /**
     * 反注册目标
     * @param host 目标对象实例
     */
    public void unregister(Object host){
        mEvents.unregister(host);
    }

    /**
     * 发出一个Action事件
     * @param action Action
     */
    public void emit(Action action){
        putCallStack(action);
        mEvents.emit(action.type, action);
    }

    /**
     * 导出Events的接口
     * @param listener OnTargetMissListener<Meta>
     */
    public void setOnTargetMissListener(OnTargetMissListener<Meta> listener) {
        mEvents.setOnTargetMissListener(listener);
    }

    /**
     * 设置是否开启调试模式。如果开启调试模式，Dispatcher 会在Action中记录提交事件的调用栈
     * @param enabled 是否开启调试模式
     */
    public void setTraceEnabled(boolean enabled) {
        mTraceEnabled = enabled;
    }

    private void putCallStack(Action action) {
        // 记录回调方法栈
        if (mTraceEnabled) {
            action.setSenderStack(CallStack.collect());
        }else{
            action.setSenderStack(CALL_STACK_WARN);
        }
    }

}
