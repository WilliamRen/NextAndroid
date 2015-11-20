package com.github.yoojia.next.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.github.yoojia.next.lang.FieldsFinder;
import com.github.yoojia.next.lang.Filter;
import com.github.yoojia.next.lang.Objects;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 注入View加载类
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class NextAutoView {

    private static final String TAG = NextAutoView.class.getSimpleName();

    private final Object mHost;
    private final Class<?> mHostType;

    public NextAutoView(Object host) {
        mHost = host;
        mHostType = host.getClass();
    }

    public void inject(Finder viewField){
        final FieldsFinder fieldsFinder = new FieldsFinder();
        fieldsFinder.filter(new Filter<Field>() {
            @Override
            public boolean accept(Field field) {
                return field.isAnnotationPresent(AutoView.class);
            }
        });
        final List<Field> fields = fieldsFinder.find(mHostType);
        if (fields.isEmpty()){
            Log.d(TAG, "- Empty Views(with @AutoView) ! ");
            Warning.show(TAG);
        }else{
            final Objects os = new Objects(mHost);
            for (Field field : fields){
                final boolean origin = field.isAccessible();
                field.setAccessible(true);
                final AutoView ano = field.getAnnotation(AutoView.class);
                os.setField(field, viewField.find(ano.value(), ano.parents()));
            }
        }
    }

    public void inject(Activity activity){
        inject(new ActivityFinder(activity));
    }

    public void inject(View view){
        inject(new ViewFinder(view));
    }

    public interface Finder {
        View find(int viewId, int[] route);
    }

    /**
     * 绑定扫描扫描对象及扫描停止类型
     * @param host 目标对象
     * @return NextAutoView
     */
    public static NextAutoView use(Object host){
        return new NextAutoView(host);
    }
}
