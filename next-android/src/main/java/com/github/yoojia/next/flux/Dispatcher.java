package com.github.yoojia.next.flux;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.Schedulers;
import com.github.yoojia.next.events.Subscriber;
import com.github.yoojia.next.lang.CallStack;
import com.github.yoojia.next.lang.Filter;

import java.lang.reflect.Method;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Dispatcher {

    private static final String STACK_WARNING = "Set dispatcher.setDebugEnabled(true) to collect methods stack !";

    private boolean mDebugEnabled = false;

    private final NextEvents mEvents;

    public Dispatcher(Schedulers schedulers) {
        mEvents = new NextEvents(schedulers, "FluxDispatcher");
    }

    public Dispatcher() {
        this(Schedulers.main());
    }

    /**
     * 扫描目标并注册其@Subscribe方法
     * @param host 目标对象实例
     */
    public void register(Object host){
        mEvents.subscribe(host, new ActionMethodFilter());
    }

    /**
     * 反注册目标
     * @param host 目标对象实例
     */
    public void unregister(Object host){
        mEvents.unsubscribe(host);
    }

    /**
     * 设置事件订阅接口，并指定是否异步及订阅的事件
     * @param subscriber 订阅接口
     * @param async 是否异步执行
     * @param actions 订阅事件
     */
    public void subscribe(Subscriber subscriber, boolean async, Actions actions) {
        mEvents.subscribe(subscriber, async, actions.events());
    }

    /**
     * 反注册事件订阅接口
     * @param subscriber 事件订阅接口
     */
    public void unsubscribe(Subscriber subscriber) {
        mEvents.unsubscribe(subscriber);
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

    /**
     * 设置是否开启调试模式。如果开启调试模式，Dispatcher 会在Action中记录提交事件的调用栈
     * @param enabled 是否开启调试模式
     */
    public void setDebugEnabled(boolean enabled) {
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
