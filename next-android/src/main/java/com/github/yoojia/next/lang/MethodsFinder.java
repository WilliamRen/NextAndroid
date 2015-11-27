package com.github.yoojia.next.lang;


import java.lang.reflect.Method;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class MethodsFinder extends AnnotatedFinder<Method> {

    public MethodsFinder() {
        super();
        filter(new Filter<Method>() {
            @Override
            public boolean accept(Method method) {
                // Not bridge or synthetic methods
                return !method.isBridge() && !method.isSynthetic();
            }
        });
    }

    @Override
    protected Method[] resourcesFromType(Class<?> type) {
        return type.getDeclaredMethods();
    }

}
