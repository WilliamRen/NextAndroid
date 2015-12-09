package com.github.yoojia.next.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.github.yoojia.next.lang.FieldsFinder;
import com.github.yoojia.next.lang.Filter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 注入View加载类
 * @author 陈小锅 (yoojiachen@gmail.com)
 * @since 1.0
 */
public class NextAutoView {

    private static final String TAG = NextAutoView.class.getSimpleName();

    private final Object mTarget;
    private final Class<?> mTargetType;

    public NextAutoView(Object target) {
        mTarget = target;
        mTargetType = target.getClass();
    }

    public void inject(Finder viewField){
        final List<Field> fields = new FieldsFinder()
                .filter(new AutoViewFilter())
                .find(mTargetType);
        if (fields.isEmpty()){
            Log.d(TAG, "- Empty Views(with @AutoView) ! Target Object: " + mTarget);
            Warning.show(TAG);
        }else{
            for (Field field : fields){
                field.setAccessible(true);
                final AutoView av = field.getAnnotation(AutoView.class);
                try {
                    final View view = viewField.find(av.value(), av.parents());
                    field.set(mTarget, view);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
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

    private static class AutoViewFilter implements Filter<Field> {

        private static boolean isAutoViewField(Field field) {
            if (field.isSynthetic() || field.isEnumConstant()) {
                return false;
            }
            // Check View type
            final Class<?> type = field.getType();
            if (! View.class.isAssignableFrom(type)) {
                return false;
            }
            return field.isAnnotationPresent(AutoView.class);
        }

        @Override
        public boolean accept(Field field) {
            return isAutoViewField(field);
        }
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
