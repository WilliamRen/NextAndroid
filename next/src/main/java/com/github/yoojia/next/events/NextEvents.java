package com.github.yoojia.next.events;

import android.util.Log;

import com.github.yoojia.next.lang.ImmutableObject;
import com.github.yoojia.next.lang.MethodsFinder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-10-11
 */
public class NextEvents {

    protected final String mTag;

    private final AtomicInteger mSubmitCounter = new AtomicInteger(0);

    private final ExecutorService mThreads;
    private final Class<?> mDefStopAtParentType;
    private final Reactor mReactor = new Reactor();
    private final ImmutableObject<OnErrorsListener> mOnErrorsListener = new ImmutableObject<>();

    /**
     * 使用指定线程池来处理事件。
     * @param threads 线程池实现
     * @param tag NextEvent 实例标签名
     * @param stopAtParentType 默认扫描 @Subscribe 方法时的停止超类类型
     */
    public NextEvents(ExecutorService threads, String tag, Class<?> stopAtParentType){
        mTag = tag;
        mDefStopAtParentType = stopAtParentType;
        mThreads = threads;
    }

    /**
     * 使用固定大小的线程池来处理事件。
     * 注意： 固定大小线程池的处理策略是当线程池繁忙时，抛弃后续处理任务。
     * @param threads 指定固定线程池大小
     * @param tag NextEvent 实例标签名
     * @param stopAtParentType 默认扫描 @Subscribe 方法时的停止超类类型
     */
    public NextEvents(int threads, String tag, Class<?> stopAtParentType) {
        this(Executors.newFixedThreadPool(threads), tag, stopAtParentType);
    }

    /**
     * 使用动态大小线程池来处理事件。
     * @param tag NextEvent 实例标签名
     * @param stopAtParentType 默认扫描 @Subscribe 方法时的停止超类类型
     */
    public NextEvents(String tag, Class<?> stopAtParentType) {
        this(Executors.newCachedThreadPool(), tag, stopAtParentType);
    }

    /**
     * 将目标对象实例注册到 NextEvents 中，NextEvents 将扫描目标对象及其超类中所有添加 @Subscribe 注解的方法，并注册管理。
     * 注意：目标对象实例及 @Subscribe 注解的方法将被强引用。
     * @param targetHost 需要被注册的目标对象实例
     * @param stopAtParentType 扫描 @Subscribe 方法时的停止超类类型。
     *                         例如： NextEvents 的超类为 Object，则其停止超类类型为 Object.class
     */
    final public void register(Object targetHost, Class<?> stopAtParentType) {
        final long startScan = System.nanoTime();
        final List<Method> methods = new MethodsFinder(targetHost.getClass(), stopAtParentType).filter(Subscribe.class);
        timeLogging("SCAN[@Subscribe]", startScan);
        if (methods.isEmpty()){
            Log.e(mTag, "Empty Handlers(with @Subscribe) !");
            Warning.show(mTag);
        }else{
            final long startRegister = System.nanoTime();
            for (Method method : methods){
                final Subscribe conf = method.getAnnotation(Subscribe.class);
                final EventWrap[] events = wrap(method);
                final String[] eventsInOrder = new String[events.length];
                for (int i = 0; i < events.length; i++) {
                    eventsInOrder[i] = events[i].event;
                }
                final MethodTarget target = new MethodTarget(eventsInOrder, conf.async(), targetHost, method);
                mReactor.register(target, events);
            }
            timeLogging("REGISTER", startRegister);
        }
    }

    /**
     * 反注册目标对象。所有被注册管理的目标对象和方法，如果其目标对象与 targetHost 内存地址相同，则被注销，解除对象和方法的强引用。
     * @param targetHost 需要反注册的目标对象
     */
    final public void unregister(final Object targetHost) {
        final Runnable task = new Runnable() {
            @Override public void run() {
                mReactor.unregister(targetHost);
            }
        };
        mThreads.execute(task);
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
     * @throws NullPointerException 如果事件对象或者事件名为空，将抛出 NullPointerException
     */
    final public void emit(final Object eventObject, final String eventName) {
        if (eventObject == null || eventName == null || eventName.isEmpty()) {
            throw new NullPointerException("Event OBJECT or NAME must not be null or empty !");
        }
        final List<Reactor.Trigger> trigger = mReactor.emit(eventName, eventObject);
        mSubmitCounter.addAndGet(trigger.size());
        for (final Reactor.Trigger item : trigger){
            final Runnable task = new Runnable() {
                @Override public void run(){
                    try {
                        item.invoke();
                    } catch (Exception error) {
                        if (mOnErrorsListener.has()) {
                            mOnErrorsListener.get().onErrors(error);
                        }else{
                            error.printStackTrace();
                        }
                    }
                }
            };
            trySubmitTask(task);
        }
    }

    protected void trySubmitTask(Runnable task){
        mThreads.submit(task);
    }

    /**
     * 关闭 NextEvents，销毁线程池。
     */
    final public void shutdown() {
        Log.d(mTag, "- Trigger events count: " + getTriggeredCount());
        Log.d(mTag, "- Submit tasks count:   " + getSubmitCount());
        final int override = getOverrideCount();
        if (override > 0) {
            Log.w(mTag, "- Override events count:" + override);
        }
        final int deadEvents = getDeadEventsCount();
        if (deadEvents > 0) {
            Log.e(mTag, "- [!!]Dead events count:" + getDeadEventsCount());
        }
        mThreads.shutdown();
    }

    /**
     * 登录默认扫描 @Subscribe 方法时的停止超类类型来注册目标对象实例
     * @param targetHost 目标对象实例
     */
    final public void register(Object targetHost) {
        register(targetHost, mDefStopAtParentType);
    }

    /**
     * 异步地注册
     * @param targetHost 需要被注册的目标对象实例
     */
    final public void registerAsync(final Object targetHost) {
        registerAsync(targetHost, mDefStopAtParentType);
    }

    /**
     * 指定扫描 @Subscribe 方法时的停止超类类型，异步地注册
     * @param targetHost 需要被注册的目标对象实例
     * @param stopAtParentType 扫描 @Subscribe 方法时的停止超类类型
     */
    final public void registerAsync(final Object targetHost, final Class<?> stopAtParentType) {
        final Runnable task = new Runnable() {
            @Override public void run() {
                register(targetHost, stopAtParentType);
            }
        };
        mThreads.execute(task);
    }

    /**
     * 获取提交执行任务的次数
     * @return 提交执行任务的次数
     */
    public int getSubmitCount() {
        return mSubmitCounter.get();
    }

    /**
     * 获取目标方法被触发的次数
     * @return 目标方法被触发的次数
     */
    public int getTriggeredCount() {
        return mReactor.getTriggeredCount();
    }

    /**
     * 获取被覆盖的事件的次数
     * @return 被覆盖的事件的次数
     */
    public int getOverrideCount() {
        return mReactor.getOverrideCount();
    }

    /**
     * 获取无执行目标方法的事件次数
     * @return 无执行目标方法的事件次数
     */
    public int getDeadEventsCount() {
        return mReactor.getDeadEventsCount();
    }

    /**
     * 设置执行目标方法发生的错误的处理回调接口
     * @param listener 处理回调接口
     */
    public void setOnErrorsListener(OnErrorsListener listener) {
        mOnErrorsListener.setOnce(listener);
    }

    private EventWrap[] wrap(Method method){
        final Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) {
            throw new IllegalArgumentException("Require ONE or MORE params in method: " + method);
        }
        final Annotation[][] matrix = method.getParameterAnnotations();
        final EventWrap[] events = new EventWrap[types.length];
        for (int i = 0; i < types.length; i++) {
            final Annotation[] annotations = matrix[i];
            if (annotations.length == 0) {
                throw new IllegalArgumentException("All params must has a @Event annotation in method: " + method);
            }
            final Event event = (Event) annotations[0];
            events[i] = new EventWrap(event.value(), types[i]);
        }
        return events;
    }

    private void timeLogging(String message, long start) {
        final float delta = (System.nanoTime() - start) / 1000000.0f;
        if (delta < 5) return;
        final String time = String.format("%.3f", delta)  + "ms";
        Log.d(mTag, "[" + time + "](>5ms)\t" + message);
    }
}
