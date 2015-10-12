package com.github.yoojia.next.lang;

import java.lang.reflect.Field;

/**
 * Field helper
 * @author 陈小锅 (chenyongjia@parkingwang.com)
 * @since 1.0
 */
public class FieldsFinder extends AnnotatedFinder<Field> {

    public FieldsFinder(Class<?> currentHostType, Class<?> stopAtParentType) {
        super(currentHostType, stopAtParentType);
    }

    public FieldsFinder(Class<?> currentHostType) {
        super(currentHostType);
    }

    @Override
    protected Field[] resourcesFromType(Class<?> type) {
        return type.getDeclaredFields();
    }

}
