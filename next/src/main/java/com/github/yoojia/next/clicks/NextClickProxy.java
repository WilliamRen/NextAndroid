package com.github.yoojia.next.clicks;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.UIThreadEvents;
import com.github.yoojia.next.lang.FieldsFinder;
import com.github.yoojia.next.lang.Objects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextClickProxy {

    private static final String TAG = "CLICKS";

    private final Class<?> mStopAtParentType;
    private final SparseArray<View> mKeyCodeMapping = new SparseArray<>();
    private final NextEvents mEvents;

    public NextClickProxy(Class<?> stopAtParentType) {
        this.mStopAtParentType = stopAtParentType;
        mEvents = new UIThreadEvents(Runtime.getRuntime().availableProcessors(), TAG, stopAtParentType);
    }

    public void register(final Object host){
        final Runnable task = new Runnable() {
            @Override public void run() {
                final List<Field> fields = new FieldsFinder(host.getClass(), mStopAtParentType).filter(EmitClick.class);
                if (fields.isEmpty()){
                    Log.d(TAG, "- Empty Handlers(with @EmitClick) ! ");
                    Warning.show(TAG);
                }else{
                    for (Field field : fields){
                        final EmitClick evt = field.getAnnotation(EmitClick.class);
                        try {
                            final View view = bindClickView(host, field, evt.event());
                            if (Integer.MIN_VALUE != evt.keyCode()) {
                                mKeyCodeMapping.append(evt.keyCode(), view);
                            }
                        } catch (Exception error) {
                            throw new IllegalStateException(error);
                        }
                    }
                    // 只注册管理参数为ClickEvent类型的方法
                    final NextEvents.Filter filter = new NextEvents.Filter() {
                        @Override public boolean is(Method method) {
                            // 点击只接受一个事件,并且只能是ClickEvent类型
                            final Class<?>[] types = method.getParameterTypes();
                            if (types.length != 1) {
                                return false;
                            }
                            return ClickEvent.class.equals(types[0]);
                        }
                    };
                    mEvents.registerAsync(host, filter);
                }
            }
        };
        new Thread(task).start();
    }

    public void emitKeyCode(int keyCode) {
        View view = mKeyCodeMapping.get(keyCode);
        if (view != null) {
            view.performClick();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends View> void emitClick(T view, String event){
        mEvents.emit(new ClickEvent(view), event, false/*Not allow deviate*/);
    }

    private View bindClickView(Object host, Field field, final String event) throws Exception {
        field.setAccessible(true);
        final Object viewField = field.get(host);
        if (viewField instanceof View){
            final View view = (View) viewField;
            view.setOnClickListener(new View.OnClickListener() {
                @Override @SuppressWarnings("unchecked")
                public void onClick(View v) {
                    emitClick(v, event);
                }
            });
            return view;
        }else{
            throw new IllegalArgumentException("@EmitClick field not a View: " + field);
        }
    }

    public static NextClickProxy bind(Object host, Class<?> rootType){
        NextClickProxy proxy = new NextClickProxy(rootType);
        proxy.register(host);
        return proxy;
    }

    public static NextClickProxy bindAndroid(Object host) {
        final Class<?> androidParent = Objects.findAndroidParent(host.getClass());
        if (androidParent == null) {
            throw new IllegalArgumentException("Object is not a sub class inherit from Android Framework !");
        }
        return bind(host, androidParent);
    }

    public static NextClickProxy bindActivity(Object host){
        return bind(host, Activity.class);
    }

}
