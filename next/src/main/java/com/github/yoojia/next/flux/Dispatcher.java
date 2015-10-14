package com.github.yoojia.next.flux;

import android.app.Activity;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.UIThreadEvents;

/**
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public final class Dispatcher {

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
        mEvents.register(host);
    }

    /**
     * 异步地扫描并注册
     * @param host 目标对象实例
     */
    public void registerAsync(Object host){
        checkType(host.getClass());
        mEvents.registerAsync(host);
    }

    /**
     * 扫描目标,并指定其它扫描停止类型
     * @param host 目标对象实例
     * @param stopAtParentType 指定其它扫描停止类型
     */
    void registerWithStopType(Object host, Class<?> stopAtParentType){
        mEvents.register(host, stopAtParentType);
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
        mEvents.emit(action, action.type, false/* not allow deviate*/);
    }

    /**
     * 提交事件,并且允许事件没有目标
     * @param action Action 事件
     */
    public void emitLeniently(Action action){
        mEvents.emit(action, action.type);
    }

    public void shutdown(){
        mEvents.shutdown();
    }

    private void checkType(Class<?> hostType) {
        if ( ! mStopAtParentType.isAssignableFrom(hostType)) {
            throw new IllegalArgumentException("Host type(" + hostType.getName()
                    + ") is not a type of given parent type: " + mStopAtParentType);
        }
    }

    public static Dispatcher newActivity(){
        return new Dispatcher(Activity.class);
    }
}
