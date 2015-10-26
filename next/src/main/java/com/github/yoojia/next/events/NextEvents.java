package com.github.yoojia.next.events;

import android.util.Log;

import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.lang.QuantumObject;
import com.github.yoojia.next.lang.MethodsFinder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.yoojia.next.lang.Preconditions.notEmpty;
import static com.github.yoojia.next.lang.Preconditions.notNull;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
public class NextEvents {

    protected final String mTag;

    private final AtomicInteger mSubmitCount = new AtomicInteger(0);
    private final FinalInvokers mInvokers;
    private final Reactor mReactor = new Reactor();
    private final QuantumObject<OnErrorsListener> mOnErrorsListener = new QuantumObject<>();

    /**
     * 使用指定线程池来处理事件。
     * @param workerThreads 工作线程池
     * @param tag NextEvent 实例标签名
     */
    public NextEvents(ExecutorService workerThreads, String tag){
        mTag = tag;
        mInvokers = new FinalInvokers(workerThreads);
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
            @Override
            public boolean accept(Method method) {
                return method.isAnnotationPresent(Subscribe.class);
            }
        });
        final List<Method> annotated = finder.find(targetHost.getClass());
        timeLogging("SCAN[@Subscribe]", startScan);
        final long startRegister = System.nanoTime();
        final Register reg = new Register(mTag, mReactor, targetHost);
        reg.register(annotated, filter);
        timeLogging("REGISTER", startRegister);
        if (annotated.isEmpty()){
            Log.e(mTag, "Empty Handlers(with @Subscribe) !");
            Warning.show(mTag);
        }
    }

    /**
     * 反注册目标对象。所有被注册管理的目标对象和方法，如果其目标对象与 targetHost 内存地址相同，则被注销，解除对象和方法的强引用。
     * @param targetHost 需要反注册的目标对象
     */
    public void unregister(final Object targetHost) {
        notNull(targetHost, "Subscriber host must not be null !");
        mReactor.unregister(targetHost);
    }

    /**
     * 提交事件。
     * 被注册管理的方法中，如果 @Event(EVENT-NAME) 中的 EVENT-NAME 与提交的事件名相同，并且方法声明的全部事件都已提交，
     * 则该方法被触发并由线程池执行。
     *
     * ### 事件被触发的详细条件：
     *   - 事件名相同
     *   - 事件类型相同。如果是 Primitive 类型，则为封装类相同。
     *
     * ### 注意：
     *   - 如果目标方法定义多个事件，仅当全部事件都提交后才会被触发执行；
     *   - 如果目标方法定义多个事件，相同事件名的事件将覆盖已提交的事件；
     *
     * @param eventObject 事件对象
     * @param eventName 事件名
     * @param lenient 是否允许事件没有目标. 如果为false, 当事件没有目标接受时,会抛出异常.
     * @throws NullPointerException 如果事件对象或者事件名为空，将抛出 NullPointerException
     */
    public void emit(final Object eventObject, final String eventName, final boolean lenient) {
        notNull(eventObject, "Event object must not be null !");
        notEmpty(eventName, "Event name must not be null !");
        final List<Reactor.Trigger> triggers = mReactor.emit(eventName, eventObject, lenient);
        mSubmitCount.addAndGet(triggers.size());
        for (final Reactor.Trigger trigger : triggers){
            final Callable<Void> task = new Callable<Void>() {
                @Override public Void call() throws Exception {
                    try {
                        trigger.invoke();
                    } catch (Exception error) {
                        if (mOnErrorsListener.watch()) {
                            mOnErrorsListener.get().onErrors(error);
                        }else{
                            throw error;
                        }
                    }
                    return null;
                }
            };
            if (trigger.async) {
                mInvokers.invokeInMainThread(task);
            }else{
                mInvokers.invokeInThreads(task);
            }
        }
    }

    /**
     * 允许事件没有目标
     * @param eventObject 事件对象
     * @param eventName 事件名
     */
    public void emitLeniently(final Object eventObject, final String eventName) {
        emit(eventObject, eventName, true);
    }

    /**
     * 设置执行目标方法发生的错误的处理回调接口
     * @param listener 处理回调接口
     */
    public void setOnErrorsListener(OnErrorsListener listener) {
        mOnErrorsListener.set(listener);
    }

    /**
     * 输出事件统计数据
     */
    public void printEventsStatistics() {
        Log.d(mTag, "- Trigger events count: " + mReactor.getTriggeredCount());
        Log.d(mTag, "- Submit tasks count:   " + mSubmitCount.get());
        Log.w(mTag, "- Override events count:" + mReactor.getOverrideCount());
        Log.e(mTag, "- [!!]Dead events count:" + mReactor.getDeadEventsCount());
    }

    private void timeLogging(String message, long start) {
        final float delta = (System.nanoTime() - start) / 1000000.0f;
        if (delta < 5) return;
        final String time = String.format("%.3f", delta)  + "ms";
        Log.d(mTag, "[" + time + "](>5ms)\t" + message);
    }

}
