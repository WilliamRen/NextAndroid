package com.github.yoojia.next.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public abstract class AnnotatedFinder<T extends AnnotatedElement> {

    private final Class<?> mCurrentTargetType;

    /**
     * 注解内容查找器。
     * @param currentTargetType 当前目标类类型
     */
    public AnnotatedFinder(Class<?> currentTargetType) {
        if (currentTargetType == null) {
            throw new NullPointerException("Current target class type must not be null !");
        }
        mCurrentTargetType = currentTargetType;
    }

    public List<T> filter(final Class<? extends Annotation> type){
        if (type == null) {
            throw new NullPointerException("Annotation type must not be null !");
        }
        final Filter<T> filter = new Filter<T>() {

            @Override
            public boolean acceptResource(T res) {
                return AnnotatedFinder.this.acceptResource(res, type);
            }

            @Override
            public boolean acceptType(Class<?> type) {
                final String className = type.getName();
                if (className.startsWith("java.")) {
                    return false;
                }else if (className.startsWith("javax.")) {
                    return false;
                }else if (className.startsWith("android.")) {
                    return false;
                }else {
                    return AnnotatedFinder.this.acceptType(type);
                }
            }
        };
        return filterWith(filter);
    }

    public List<T> filterWith(Filter<T> filter){
        if (filter == null) {
            throw new NullPointerException("Filter must not be null !");
        }
        final List<T> output = new ArrayList<>();
        Class<?> type = mCurrentTargetType;
        while (! Object.class.equals(type)){
            // Check type
            if ( ! filter.acceptType(type)) {
                break;
            }
            final T[] resources = resourcesFromType(type);
            for (T res : resources){
                if(filter.acceptResource(res)){
                    output.add(res);
                }
            }
            type = type.getSuperclass();
        }
        return output;
    }

    protected boolean acceptResource(T itemObject, Class<? extends Annotation> annotationType){
        return itemObject.isAnnotationPresent(annotationType);
    }

    protected boolean acceptType(Class<?> type) {
        return true;
    }

    public interface Filter<T> {

        boolean acceptResource(T res);

        boolean acceptType(Class<?> type);
    }

    protected abstract T[] resourcesFromType(Class<?> type);

}
