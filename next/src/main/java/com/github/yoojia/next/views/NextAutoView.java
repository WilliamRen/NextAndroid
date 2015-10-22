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

    private static final String TAG = NextAutoView.class.getSimpleName();

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
                final boolean origin = field.isAccessible();
                field.setAccessible(true);
                final AutoView ano = field.getAnnotation(AutoView.class);
                if (!origin) {
                    field.setAccessible(false);
                }
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

    /**
     * 绑定Activity. NextAutoView会扫描目标对象及其父类,直到Activity类型为止.
     * @param host 目标对象
     * @return NextAutoView对象
     */
    public static NextAutoView useActivity(Object host){
        if (host instanceof Activity) {
            return use(host, Activity.class);
        }else {
            throw new IllegalArgumentException("Host object is not an Activity object !");
        }
    }

    /**
     * 绑定Android框架的类型. NextAutoView会扫描目标对象及其父类, 直到其父类为Android Framework的类型为止.
     * @param host 目标对象
     * @return NextAutoView对象
     */
    public static NextAutoView useAndroid(Object host) {
        final Class<?> androidParent = Objects.findAndroidParent(host.getClass());
        if (androidParent == null) {
            throw new IllegalArgumentException("Object is not a sub class inherit from Android Framework !");
        }
        return use(host, androidParent);
    }

    /**
     * 绑定扫描扫描对象及扫描停止类型
     * @param host 目标对象
     * @param rootType 扫描停止类型
     * @return NextAutoView
     */
    public static NextAutoView use(Object host, Class<?> rootType){
        return new NextAutoView(host, rootType);
    }
}
