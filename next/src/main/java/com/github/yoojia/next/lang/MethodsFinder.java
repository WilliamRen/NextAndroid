package com.github.yoojia.next.lang;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public class MethodsFinder extends AnnotatedFinder<Method> {

    public MethodsFinder(Class<?> currentHostType, Class<?> stopAtParentType) {
        super(currentHostType, stopAtParentType);
    }

    public MethodsFinder(Class<?> currentHostType) {
        super(currentHostType);
    }

    @Override
    protected boolean isAccepted(Method itemObject, Class<? extends Annotation> annotationType) {
        if (itemObject.isBridge()) {
            return false;
        }else{
            // Super will check annotation
            return super.isAccepted(itemObject, annotationType);
        }
    }

    @Override
    protected Method[] resourcesFromType(Class<?> type) {
        return type.getDeclaredMethods();
    }

}
