package com.github.yoojia.next.flux;

import com.github.yoojia.next.events.FilterMethods;
import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.lang.CallStack;
import com.github.yoojia.next.react.Schedule;

import java.lang.reflect.Method;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Dispatcher {

    private static final String STACK_WARNING = "Use <dispatcher.setTraceEnabled(...)> to collect methods stack !";

    private boolean mTraceEnabled = false;

    private final NextEvents mEvents;

    public Dispatcher(Schedule schedulers) {
        mEvents = new NextEvents();
        mEvents.subscribeOn(schedulers);
    }

    public Dispatcher() {
        mEvents = new NextEvents();
    }

    /**
     * 扫描目标并注册其@Subscribe方法
     * @param host 目标对象实例
     */
    public void register(Object host){
        mEvents.register(host, new FilterMethods.Filter() {
            // Accept All types
            @Override public boolean acceptType(Class<?> type) {
                return true;
            }
            // Only accept Action type
            @Override public boolean acceptMethod(Method method) {
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
     * 安全销毁
     */
    public void destroy(){
        mEvents.close();
    }

    /**
     * 提交Action事件
     * @param action Action 事件
     */
    public void emit(Action action){
        logCallStack(action);
        mEvents.emit(action.type, action);
    }

    /**
     * 设置是否开启调试模式。如果开启调试模式，Dispatcher 会在Action中记录提交事件的调用栈
     * @param enabled 是否开启调试模式
     */
    public void setTraceEnabled(boolean enabled) {
        mTraceEnabled = enabled;
    }

    private void logCallStack(Action action) {
        // 记录回调方法栈
        if (mTraceEnabled) {
            action.setSenderStack(CallStack.collect());
        }else{
            action.setSenderStack(STACK_WARNING);
        }
    }

}
