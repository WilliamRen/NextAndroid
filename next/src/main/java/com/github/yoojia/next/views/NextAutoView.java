package com.github.yoojia.next.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.github.yoojia.next.lang.FieldsFinder;
import com.github.yoojia.next.lang.Objects;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 注入View加载类
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class NextAutoView {

    private static final String TAG = "AUTO-VIEW";

    private final Object mHost;
    private final Class<?> mRootType;
    private final Class<?> mHostType;

    public NextAutoView(Object host, Class<?> rootType) {
        mHost = host;
        mHostType = host.getClass();
        mRootType = rootType;
    }

    public void inject(Finder finder){
        final List<Field> fields = new FieldsFinder(mHostType, mRootType).filter(AutoView.class);
        if (fields.isEmpty()){
            Log.d(TAG, "- Empty Views(with @AutoView) ! ");
            Warning.show(TAG);
        }else{
            final Objects os = new Objects(mHost);
            for (Field field : fields){
                field.setAccessible(true);
                final AutoView ano = field.getAnnotation(AutoView.class);
                os.setField(field, finder.find(ano.value(), ano.parents()));
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

    public static NextAutoView useActivity(Object host){
        return use(host, Activity.class);
    }

    public static NextAutoView use(Object host, Class<?> rootType){
        return new NextAutoView(host, rootType);
    }
}
