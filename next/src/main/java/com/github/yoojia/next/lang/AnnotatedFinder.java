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

    protected Map<T> mResourceMap = new Map<T>() {
        @Override
        public boolean accept(T res) {
            return true;
        }
    };

    protected Filter mTypeFilter = new Filter() {
        @Override
        public boolean accept(Class<?> type) {
            final String className = type.getName();
            if (className.startsWith("java.")
                    || className.startsWith("javax.")
                    || className.startsWith("android.")) {
                return false;
            }else{
                return true;
            }
        }
    };

    public AnnotatedFinder<T> map(Map<T> map) {
        notNull(map, "Resource map must not be null !");
        mResourceMap = map;
        return this;
    }

    public AnnotatedFinder<T> filter(Filter filter) {
        notNull(filter, "Resource map must not be null !");
        mTypeFilter = filter;
        return this;
    }

    public List<T> find(Class<?> targetType) {
        notNull(targetType, "Target type must not be null !");
        final List<T> output = new ArrayList<>();
        Class<?> type = targetType;
        while (! Object.class.equals(type)){
            // Check type
            if (!mTypeFilter.accept(type)) {
                break;
            }
            final T[] resources = resourcesFromType(type);
            for (T res : resources){
                if(mResourceMap.accept(res)){
                    output.add(res);
                }
            }
            type = type.getSuperclass();
        }
        return output;
    }

    protected abstract T[] resourcesFromType(Class<?> type);

    public interface Map<T> {
        /**
         * return TRUE if accept this RESOURCE
         */
        boolean accept(T res);
    }

    public interface Filter {
        /**
         * return TRUE if accept this TYPE
         */
        boolean accept(Class<?> type);
    }
}
