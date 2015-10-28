package com.github.yoojia.next.flux;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.Threads;
import com.github.yoojia.next.lang.CallStack;
import com.github.yoojia.next.lang.Filter;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Dispatcher {

    private static final String STACK_WARNING = "Set dispatcher.enabledDebug(true) to collect methods stack !";

    private boolean mDebugEnabled = false;

    private final NextEvents mEvents;

    public Dispatcher(ExecutorService threads) {
        mEvents = new NextEvents(threads, "FluxDispatcher");
    }

    public Dispatcher() {
        this(Threads.CPUx2());
    }

    /**
     * 扫描目标并注册其@Subscribe方法
     * @param host 目标对象实例
     */
    public void register(Object host){
        mEvents.register(host, new ActionMethodFilter());
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
        mEvents.destroy();
    }

    /**
     * 提交Action事件, 不允许事件没有目标
     * @param action Action 事件
     */
    public void emit(Action action){
        logCallStack(action);
        mEvents.emit(action, action.type, false/* not allow deviate*/);
    }

    /**
     * 提交事件,并且允许事件没有目标
     * @param action Action 事件
     */
    public void emitLeniently(Action action){
        logCallStack(action);
        mEvents.emit(action, action.type, true/* leniently */);
    }

    public void enabledDebug(boolean enabled) {
        mDebugEnabled = enabled;
    }

    private void logCallStack(Action action) {
        // 记录回调方法栈
        if (mDebugEnabled) {
            final String callStackInfo = CallStack.collect();
            action.setSenderStack(callStackInfo);
        }else{
            action.setSenderStack(STACK_WARNING);
        }
    }

    private static class ActionMethodFilter implements Filter<Method> {

        @Override public boolean accept(Method method) {
            // 全部类型都只能是Action类型
            final Class<?>[] types = method.getParameterTypes();
            for (Class<?> type : types) {
                if ( ! Action.class.equals(type)) {
                    return false;
                }
            }
            return true;
        }
    }

}
