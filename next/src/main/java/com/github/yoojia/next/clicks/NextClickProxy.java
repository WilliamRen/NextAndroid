package com.github.yoojia.next.clicks;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.github.yoojia.next.events.NextEvents;
import com.github.yoojia.next.events.UIThreadEvents;
import com.github.yoojia.next.lang.FieldsFinder;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class NextClickProxy {

    private static final String TAG = "CLICKS";

    private final Class<?> mStopAtParentType;
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
                            tryBindClick(host, field, evt.event());
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }
                    mEvents.registerAsync(host);
                }
            }
        };
        new Thread(task).start();
    }

    @SuppressWarnings("unchecked")
    public <T extends View> void emitClick(T view, String event){
        mEvents.emit(new ClickEvent(view), event);
    }

    private void tryBindClick(Object host, Field field, final String event) throws Exception {
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
        }else{
            throw new IllegalArgumentException("@EmitClick field not a View: " + field);
        }
    }

    public static NextClickProxy bind(Object host, Class<?> rootType){
        NextClickProxy proxy = new NextClickProxy(rootType);
        proxy.register(host);
        return proxy;
    }

    public static NextClickProxy bindActivity(Object host){
        return bind(host, Activity.class);
    }

}
