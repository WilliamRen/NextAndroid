package com.github.yoojia.next.flux;

import android.app.Activity;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.UIThreadEvents;
import com.github.yoojia.next.lang.CallStack;

import java.lang.reflect.Method;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Dispatcher {

    private static boolean DEBUG_ENABLED = false;

    private final Class<?> mStopAtParentType;
    private final NextEvents mEvents;

    /**
     * 构建Dispatcher,指定扫描停止类型
     * @param stopAtParentType 扫描停止类型
     */
    public Dispatcher(Class<?> stopAtParentType) {
        mStopAtParentType = stopAtParentType;
        mEvents = new UIThreadEvents(Runtime.getRuntime().availableProcessors(),
                "FluxDispatcher", stopAtParentType);
    }

    /**
     * 扫描目标并注册其@Subscribe方法
     * @param host 目标对象实例
     */
    public void register(Object host){
        checkType(host.getClass());
        mEvents.register(host, new ActionMethodFilter());
    }

    /**
     * 异步地扫描并注册
     * @param host 目标对象实例
     */
    public void registerAsync(Object host){
        checkType(host.getClass());
        mEvents.registerAsync(host, new ActionMethodFilter());
    }

    /**
     * 扫描目标,并指定其它扫描停止类型
     * @param host 目标对象实例
     * @param stopAtParentType 指定其它扫描停止类型
     */
    void registerWithStopType(Object host, Class<?> stopAtParentType){
        mEvents.register(host, stopAtParentType, new ActionMethodFilter());
    }

    /**
     * 反注册目标
     * @param host 目标对象实例
     */
    public void unregister(Object host){
        mEvents.unregister(host);
    }

    /**
     * 提交Action事件, 不允许事件没有目标
     * @param action Action 事件
     */
    public void emit(Action action){
        // 记录回调方法栈
        if (DEBUG_ENABLED) {
            final String callStackInfo = CallStack.collect();
            action.setSenderStack(callStackInfo);
        }else{
            action.setSenderStack("Set Dispatcher.debugEnabled(true) to collect methods stack !");
        }
        mEvents.emit(action, action.type, false/* not allow deviate*/);
    }

    /**
     * 提交事件,并且允许事件没有目标
     * @param action Action 事件
     */
    public void emitLeniently(Action action){
        // 记录回调方法栈
        if (DEBUG_ENABLED) {
            final String callStackInfo = CallStack.collect();
            action.setSenderStack(callStackInfo);
        }else{
            action.setSenderStack("Set Dispatcher.debugEnabled(true) to collect methods stack !");
        }
        mEvents.emit(action, action.type);
    }

    public void shutdown(){
        mEvents.shutdown();
    }

    public static void debugEnabled(boolean enabled) {
        DEBUG_ENABLED = enabled;
    }

    private void checkType(Class<?> hostType) {
        if ( ! mStopAtParentType.isAssignableFrom(hostType)) {
            throw new IllegalArgumentException("Host type(" + hostType.getName()
                    + ") is not a type of given parent type: " + mStopAtParentType);
        }
    }

    private class ActionMethodFilter implements NextEvents.Filter {

        @Override public boolean is(Method method) {
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

    public static Dispatcher newActivity(){
        return new Dispatcher(Activity.class);
    }
}
