package com.github.yoojia.next.clicks;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.lang.FieldsFinder;
import com.github.yoojia.next.lang.Filter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

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
        mEvents = new NextEvents();
    }

    public NextClickProxy register(final Object host){
        notNull(host, "Host must not be null !");
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
                return field.isAnnotationPresent(EmitClick.class);
            }
        });
        final Runnable task = new Runnable() {
            @Override public void run() {
                final List<Field> fields = finder.find(host.getClass());
                if (fields.isEmpty()){
                    Log.d(TAG, "- Empty Handlers(with @EmitClick) ! ");
                    Warning.show(TAG);
                    return;
                }
                for (Field field : fields){
                    field.setAccessible(true);
                    final EmitClick evt = field.getAnnotation(EmitClick.class);
                    try {
                        final String defineName = evt.value();
                        final View view = bindClickView(host, field, TextUtils.isEmpty(defineName) ? evt.event() : defineName);
                        if (Integer.MIN_VALUE != evt.keyCode()) {
                            mKeyCodeMapping.append(evt.keyCode(), view);
                        }
                    } catch (Exception error) {
                        throw new RuntimeException(error);
                    }
                }
                mEvents.register(host, new Filter<Method>() {
                    // 只接受ClickEvent类型的方法
                    @Override public boolean accept(Method method) {
                        final Class<?>[] types = method.getParameterTypes();
                        return ClickEvent.class.equals(types[0]);
                    }
                });
            }
        };
        // 使用匿名线程来处理点击代理的注册过程
        new Thread(task).start();
        return this;
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
            @Override @SuppressWarnings("unchecked")
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
