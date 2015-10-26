package com.github.yoojia.next.clicks;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.Schedulers;
import com.github.yoojia.next.events.UIThreadEvents;
import com.github.yoojia.next.lang.FieldsFinder;
import com.github.yoojia.next.lang.Filter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.yoojia.next.lang.Preconditions.notNull;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextClickProxy {

    private static final String TAG = "CLICKS";

    private final SparseArray<View> mKeyCodeMapping = new SparseArray<>();
    private final NextEvents mEvents;

    public NextClickProxy() {
        mEvents = new UIThreadEvents(Schedulers.CPUs, TAG);
    }

    public void register(final Object host){
        notNull(host, "Host must not be null !");
        final FieldsFinder finder = new FieldsFinder();
        finder.filter(new Filter<Field>() {
            @Override
            public boolean accept(Field field) {
                return field.isAnnotationPresent(EmitClick.class);
            }
        });
        final Runnable task = new Runnable() {
            @Override public void run() {
                final List<Field> fields = finder.find(host.getClass());
                if (fields.isEmpty()){
                    Log.d(TAG, "- Empty Handlers(with @EmitClick) ! ");
                    Warning.show(TAG);
                }else{
                    for (Field field : fields){
                        final boolean origin = field.isAccessible();
                        field.setAccessible(true);
                        final EmitClick evt = field.getAnnotation(EmitClick.class);
                        if (!origin) {
                            field.setAccessible(false);
                        }
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
                    final Filter<Method> filter = new Filter<Method>() {
                        @Override public boolean accept(Method method) {
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
        notNull(view, "View must not be null !");
        notNull(event, "Event must not be null !");
        mEvents.emit(new ClickEvent(view), event, false/*Not allow deviate*/);
    }

    private View bindClickView(Object host, Field field, final String event) throws Exception {
        final boolean origin = field.isAccessible();
        field.setAccessible(true);
        final Object viewField = field.get(host);
        if (!origin) {
            field.setAccessible(false);
        }
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

    public static NextClickProxy bind(Object host){
        NextClickProxy proxy = new NextClickProxy();
        proxy.register(host);
        return proxy;
    }

}
