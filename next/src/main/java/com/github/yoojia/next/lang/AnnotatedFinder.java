package com.github.yoojia.next.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public abstract class AnnotatedFinder<T extends AnnotatedElement> {

    private final Class<?> mCurrentTargetType;
    private final Class<?> mStopAtParentType;

    /**
     * 注解内容查找器。可以指定查找目标类父级类型深度。
     * @param currentTargetType 当前目标类类型
     * @param stopAtParentType 查找到目标类的指定父类型时，停止查找。
     */
    public AnnotatedFinder(Class<?> currentTargetType, Class<?> stopAtParentType) {
        if (currentTargetType == null) {
            throw new NullPointerException("Current target class type must not be null !");
        }
        if (stopAtParentType == null) {
            throw new NullPointerException("Stop at parent class type must not be null !");
        }
        mCurrentTargetType = currentTargetType;
        mStopAtParentType = stopAtParentType;
    }

    /**
     * 在当前类中查找注解内容
     * @param currentTargetType 当前目标类类型
     */
    public AnnotatedFinder(Class<?> currentTargetType) {
        this(currentTargetType, currentTargetType == null ? null : currentTargetType.getSuperclass());
    }

    public List<T> filter(final Class<? extends Annotation> type){
        if (type == null) {
            throw new NullPointerException("Annotation type must not be null !");
        }
        return filterWith(new Filter<T>() {
            @Override public boolean is(T item) {
                return isAnnotated(item, type);
            }
        });
    }

    public List<T> filterWith(Filter<T> filter){
        final List<T> output = new ArrayList<>();
        Class<?> type = mCurrentTargetType;
        while (! mStopAtParentType.equals(type)){
            final T[] fs = resourcesFromType(type);
            if (filter != null){
                for (T f : fs){
                    if(filter.is(f)){
                        output.add(f);
                    }
                }
            }else{
                output.addAll(Arrays.asList(fs));
            }
            type = type.getSuperclass();
        }
        return output;
    }

    protected boolean isAnnotated(T itemObject, Class<? extends Annotation> annotationType){
        return itemObject.isAnnotationPresent(annotationType);
    }

    public interface Filter<T> {
        boolean is(T item);
    }

    protected abstract T[] resourcesFromType(Class<?> type);

}
