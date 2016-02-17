package com.github.yoojia.next.clicks;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;


import com.github.yoojia.events.AndroidNextEvents;
import com.github.yoojia.events.MethodSubscriber;
import com.github.yoojia.events.NextEvents;
import com.github.yoojia.events.core.Schedule;
import com.github.yoojia.events.core.Schedules;
import com.github.yoojia.events.supports.Filter;
import com.github.yoojia.next.FieldsFinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.github.yoojia.events.supports.Preconditions.notNull;


/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextClickProxy {

    private static final String TAG = "CLICK-PROXY";

    private final SparseArray<View> mKeyCodeMapping = new SparseArray<>();
    private final ClickEvents mClickEvents;

    public NextClickProxy() {
        mClickEvents = new ClickEvents(Schedules.newCaller());
    }

    /**
     * 注册点击处理。
     * - 注册过程为同步，在目标对象的 @Click 成员变量全部被注册后返回。
     * @param target 目标对象
     * @return NextEvents
     */
    public NextClickProxy register(final Object target) {
        notNull(target, "Target object must not be null");
        final List<Field> fields = new FieldsFinder()
                .filter(ClickEvtFieldFilter.getDefault())
                .find(target.getClass());
        if (fields.isEmpty()){
            Log.e(TAG, "- Empty Fields(with @Click)! Object: " + target);
            Warning.show(TAG);
            return this;
        }
        try{
            for (Field field : fields){
                field.setAccessible(true);
                final Click evt = field.getAnnotation(Click.class);
                checkAnnotation(evt);
                final View view = createClickActionView(target, field, new View.OnClickListener() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onClick(View v) {
                        emitClick(v, evt.value());
                    }
                });
                if (KeyEvent.KEYCODE_UNKNOWN != evt.keyCode()) {
                    mKeyCodeMapping.append(evt.keyCode(), view);
                }
            }
        }catch (Exception e){
            throw new IllegalArgumentException(e);
        }
        mClickEvents.register(target, CallbackMethodFilter.getDefault());
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
        notNull(view, "View must not be null");
        notNull(event, "Event must not be null");
        mClickEvents.emit(event, new ClickEvent(view));
    }

    public NextClickProxy unregister(Object host){
        mClickEvents.unregister(host);
        return this;
    }

    private static View createClickActionView(Object host, Field field, View.OnClickListener listener) throws Exception {
        field.setAccessible(true);
        final Object viewField = field.get(host);
        final View view = (View) viewField;
        view.setOnClickListener(listener);
        return view;
    }

    private static void checkAnnotation(Click evt){
        if (TextUtils.isEmpty(evt.value())) {
            throw new IllegalArgumentException("Event name in @ClickEvent cannot be empty");
        }
    }


    public static NextClickProxy bind(Object host) {
        final NextClickProxy proxy = new NextClickProxy();
        proxy.register(host);
        return proxy;
    }

    private static class ClickEvtFieldFilter implements Filter<Field> {

        private static ClickEvtFieldFilter mDefaultFilter;

        @Override
        public boolean accept(Field field) {
            return isClickEvtField(field);
        }

        private static boolean isClickEvtField(Field field) {
            if (field.isSynthetic() || field.isEnumConstant()) {
                return false;
            }
            // Check View type
            final Class<?> type = field.getType();
            if (! View.class.isAssignableFrom(type)) {
                return false;
            }
            // Check annotation
            return field.isAnnotationPresent(Click.class);
        }

        public static ClickEvtFieldFilter getDefault(){
            synchronized (ClickEvtFieldFilter.class) {
                if (mDefaultFilter == null) {
                    mDefaultFilter = new ClickEvtFieldFilter();
                    return mDefaultFilter;
                }else {
                    return mDefaultFilter;
                }
            }
        }

    }

    private static class CallbackMethodFilter implements Filter<Method> {

        private static CallbackMethodFilter mDefaultFilter;

        @Override
        public boolean accept(Method method) {
            final Class<?>[] types = method.getParameterTypes();
            return ClickEvent.class.equals(types[0]);
        }

        public static CallbackMethodFilter getDefault(){
            synchronized (CallbackMethodFilter.class) {
                if (mDefaultFilter == null) {
                    mDefaultFilter = new CallbackMethodFilter();
                    return mDefaultFilter;
                }else {
                    return mDefaultFilter;
                }
            }
        }
    }

    /**
     * 覆盖NextEvents对@Subscriber注解的处理，并使用@ClickHandler来替换其处理过程
     */
    private class ClickEvents extends AndroidNextEvents {

        public ClickEvents(Schedule subscribeOn) {
            super(subscribeOn);
        }

        @Override
        protected boolean isSubscribeMethod(Method method) {
            // Hook call super to use @ClickHandler annotation
            if (method.isBridge() || method.isSynthetic()) {
                return false;
            }
            if (! method.isAnnotationPresent(ClickHandler.class)) {
                return false;
            }
            return true;
        }

        @Override
        protected void subscribeTargetMethod(Object object, Method method, NextEvents.InvokableMethods invokable) {
            // Hook call super to use @ClickHandler annotation
            final ClickHandler subscribe = method.getAnnotation(ClickHandler.class);
            final MethodSubscriber subscriber = new MethodSubscriber(object, method);
            invokable.add(subscriber);
            final String defineName = subscribe.on();
            final Class<?> defineType = method.getParameterTypes()[0];
            subscribe(defineName, defineType, subscriber, Schedule.FLAG_ON_CALLER_THREAD);
        }
    }

}
