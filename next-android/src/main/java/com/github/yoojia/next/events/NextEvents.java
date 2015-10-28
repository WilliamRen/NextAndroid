package com.github.yoojia.next.events;

import android.util.Log;

import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.lang.MethodsFinder;
import com.github.yoojia.next.lang.QuantumObject;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.github.yoojia.next.events.Logger.timeLog;
import static com.github.yoojia.next.lang.Preconditions.notEmpty;
import static com.github.yoojia.next.lang.Preconditions.notNull;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
public class NextEvents {

    protected final String mTag;

    private final EventsRouter mEventsRouter;
    private final ExecutorService mEmitThreads;

    private final Reactor mReactor = new Reactor();
    private final QuantumObject<OnErrorsListener> mOnErrorsListener = new QuantumObject<>();

    public NextEvents(ExecutorService workThreads, ExecutorService emitThreads, String tag){
        mTag = tag;
        mEmitThreads = emitThreads;
        mEventsRouter = new EventsRouter(workThreads, mOnErrorsListener);
    }

    public NextEvents(ExecutorService workThreads, String tag) {
        this(workThreads, Threads.CPU(), tag);
    }

    /**
     * 将目标对象实例注册到 NextEvents 中，NextEvents 将扫描目标对象及其超类中所有添加 @Subscribe 注解的方法，并注册管理。
     * 注意：目标对象实例及 @Subscribe 注解的方法将被强引用。
     * @param targetHost 需要被注册的目标对象实例
     * @param filter 对扫描后的Method作过滤处理. 通过此接口,可以过滤掉一些方法参数类型不匹配的方法.
     */
    public void register(Object targetHost, Filter<Method> filter) {
        final long startScan = System.nanoTime();
        final MethodsFinder finder = new MethodsFinder();
        finder.filter(new Filter<Method>() {
            @Override public boolean accept(Method method) {
                return method.isAnnotationPresent(Subscribe.class);
            }
        });
        final List<Method> annotated = finder.find(targetHost.getClass());
        timeLog(mTag, "EVENTS-SCAN", startScan);
        final long startRegister = System.nanoTime();
        final SubscriberRegister register = new SubscriberRegister(targetHost, new SubscriberAccess<MethodSubscriber>() {
            @Override public void access(MethodSubscriber subscriber) {
                mReactor.add(subscriber);
            }
        });
        register.batch(annotated, filter);
        timeLog(mTag, "EVENTS-REGISTER", startRegister);
        if (annotated.isEmpty()){
            Log.e(mTag, "Empty Handlers(with @Subscribe) !");
            Warning.show(mTag);
        }
    }

    /**
     * 反注册目标对象。所有被注册管理的目标对象和方法，如果其目标对象与 targetHost 内存地址相同，则被注销，解除对象和方法的强引用。
     * @param targetHost 需要反注册的目标对象
     */
    public void unregister(Object targetHost) {
        notNull(targetHost, "Subscriber host must not be null !");
        mReactor.removeByHost(targetHost);
    }

    /**
     * 提交事件并立即（阻塞）执行
     * @param eventObject 事件对象
     * @param eventName 事件名
     * @param lenient 是否允许事件没有目标. 如果为false, 当事件没有目标接受时,会抛出异常.
     */
    public void emitImmediately(Object eventObject, String eventName, boolean lenient) {
        notNull(eventObject, "Event object must not be null !");
        notEmpty(eventName, "Event name must not be null !");
        if (EventsFlags.PROCESSING) {
            Log.d(mTag, "- Emit EVENT: NAME=" + eventName + ", OBJECT=" + eventObject + ", LENIENT=" + lenient);
        }
        final long emitStart = System.nanoTime();
        final List<FuelTarget.Target> targets = mReactor.emit(eventName, eventObject, lenient);
        timeLog(mTag, "EVENTS-EMIT", emitStart);
        final long dispatchStart = System.nanoTime();
        mEventsRouter.dispatch(targets);
        timeLog(mTag, "EVENTS-DISPATCH", dispatchStart);
    }

    /**
     * 提交事件, 异步地执行
     * @param eventObject 事件对象
     * @param eventName 事件名
     * @param lenient 是否允许事件没有目标
     */
    public void emit(final Object eventObject, final String eventName, final boolean lenient) {
        mEmitThreads.submit(new Runnable() {
            @Override
            public void run() {
                emitImmediately(eventObject, eventName, lenient);
            }
        });
    }

    /**
     * 销毁 NextEvents
     */
    public void destroy(){
        mEmitThreads.shutdown();
        mEventsRouter.shutdown();
    }

    /**
     * 设置执行目标方法发生的错误的处理回调接口
     * @param listener 处理回调接口
     */
    public void setOnErrorsListener(OnErrorsListener listener) {
        mOnErrorsListener.set(listener);
    }

}