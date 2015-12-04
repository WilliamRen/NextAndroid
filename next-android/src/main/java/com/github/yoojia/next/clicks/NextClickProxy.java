package com.github.yoojia.next.clicks;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.lang.AsyncExecutor;
import com.github.yoojia.next.lang.FieldsFinder;
import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import static com.github.yoojia.next.lang.Preconditions.notNull;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextClickProxy {

    private static final String TAG = "CLICKS-PROXY";

    private final SparseArray<View> mKeyCodeMapping = new SparseArray<>();
    private final NextEvents mEvents;
    private final Schedule mSchedule;

    public NextClickProxy() {
        mSchedule = Schedules.useShared();
        mEvents = new NextEvents(mSchedule);
    }

    /**
     * 注册点击处理.
     * - 注册过程为异步处理. 此方法执行后立即返回, 不保证方法执行后点击处理注册全部成功;
     * - 必须在主线程中执行此方法;
     * @param target 目标对象
     * @return NextClickProxy
     */
    public NextClickProxy register(final Object target){
        notNull(target, "Target Object must not be null !");
        final Callable<Void> task = new Callable<Void>() {
            @Override public Void call() throws Exception {
                final FieldsFinder finder = new FieldsFinder();
                finder.filter(new Filter<Field>() {
                    @Override
                    public boolean accept(Field field) {
                        if (field.isSynthetic() || field.isEnumConstant()) {
                            return false;
                        }
                        // Check View type
                        final Class<?> type = field.getType();
                        if (! View.class.isAssignableFrom(type)) {
                            return false;
                        }
                        // Check annotation
                        return field.isAnnotationPresent(ClickEvt.class);
                    }
                });
                final List<Field> fields = finder.find(target.getClass());
                if (fields.isEmpty()){
                    Log.e(TAG, "- Empty Fields(with @ClickEvt) ! ObjectHost: " + target);
                    Warning.show(TAG);
                    return null;
                }
                for (Field field : fields){
                    field.setAccessible(true);
                    final ClickEvt evt = field.getAnnotation(ClickEvt.class);
                    try {
                        final String defineName = evt.value();
                        final View view = bindClickView(target, field, defineName);
                        if (KeyEvent.KEYCODE_UNKNOWN != evt.keyCode()) {
                            mKeyCodeMapping.append(evt.keyCode(), view);
                        }
                    } catch (Exception error) {
                        throw new RuntimeException(error);
                    }
                }
                mEvents.register(target, new Filter<Method>() {
                    // 只接受ClickEvent类型的方法
                    @Override public boolean accept(Method method) {
                        final Class<?>[] types = method.getParameterTypes();
                        return ClickEvent.class.equals(types[0]);
                    }
                });
                return null;
            }
        };
        try {
            mSchedule.submit(task, Schedule.FLAG_ON_THREADS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * 通过按键码来触发点击事件处理
     * @param keyCode 按键码
     */
    public void emitKeyCode(int keyCode) {
        View view = mKeyCodeMapping.get(keyCode);
        if (view != null) {
            view.performClick();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends View> void emitClick(T view, String event){
        notNull(view, "View must not be null !");
        notNull(event, "Event must not be null !");
        mEvents.emit(event, new ClickEvent(view));
    }

    public NextClickProxy unregister(Object host){
        mEvents.unregister(host);
        return this;
    }

    private View bindClickView(Object host, Field field, final String event) throws Exception {
        if (TextUtils.isEmpty(event)) {
            throw new IllegalArgumentException("Illegal click event name: " + event);
        }
        field.setAccessible(true);
        final Object viewField = field.get(host);
        final View view = (View) viewField;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                emitClick(v, event);
            }
        });
        return view;
    }

    /**
     * 由使用者确保只会调用一次的绑定处理
     * @param host 目标对象
     * @return NextClickProxy对象
     */
    public static NextClickProxy oneshotBind(Object host) {
        final NextClickProxy proxy = new NextClickProxy();
        proxy.register(host);
        return proxy;
    }
}
