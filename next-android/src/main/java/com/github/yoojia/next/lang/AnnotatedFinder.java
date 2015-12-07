package com.github.yoojia.next.lang;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import static com.github.yoojia.next.lang.Preconditions.notNull;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public abstract class AnnotatedFinder<T extends AnnotatedElement> {

    protected Filter<T> mResourceFilter;
    protected Filter<Class<?>> mTypeFilter;

    public AnnotatedFinder() {
        // Init Resource filter
        filter(new Filter<T>() {
            @Override
            public boolean accept(T res) {
                return true;
            }
        });
        // Init types filter
        // Ignore java,javax,android objects
        types(new Filter<Class<?>>() {
            @Override
            public boolean accept(Class<?> type) {
                final String name = type.getName();
                if (name.startsWith("java.")
                        || name.startsWith("javax.")
                        || name.startsWith("android.")) {
                    return false;
                }else{
                    return true;
                }
            }
        });
    }

    /**
     * 设置注解目标对象的过滤处理接口
     * @param filter 过滤处理接口
     * @return AnnotatedFinder
     */
    public AnnotatedFinder<T> filter(Filter<T> filter) {
        notNull(filter, "Resource filter must not be null !");
        mResourceFilter = filter;
        return this;
    }

    /**
     * 设置注解目标类型过滤的处理接口
     * @param filter 过滤处理接口
     * @return AnnotatedFinder
     */
    public AnnotatedFinder<T> types(Filter<Class<?>> filter) {
        notNull(filter, "Type filter must not be null !");
        mTypeFilter = filter;
        return this;
    }

    /**
     * 从目标类型中查找注解内容
     * @param targetType 目标类型
     * @return 注解内容列表
     */
    public List<T> find(Class<?> targetType) {
        notNull(targetType, "Subscriber type must not be null !");
        final List<T> output = new ArrayList<>();
        Class<?> type = targetType;
        while (! Object.class.equals(type)){
            // Check class type
            if (!mTypeFilter.accept(type)) {
                break;
            }
            final T[] resources = resourcesFromType(type);
            for (T res : resources){
                // Check resource
                if(mResourceFilter.accept(res)){
                    output.add(res);
                }
            }
            type = type.getSuperclass();
        }
        return output;
    }

    protected abstract T[] resourcesFromType(Class<?> type);

}
