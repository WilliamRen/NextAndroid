package com.github.yoojia.next.events;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YOOJIA.CHEN (yoojia.chen@gmail.com)
 * @version 2015-11-07
 */
public class FilterMethods {

    private final Class<?> mType;

    public FilterMethods(Object object) {
        mType = object.getClass();
    }

    public List<Method> find(Filter filter){
        final ArrayList<Method> output = new ArrayList<>();
        Class<?> type = mType;
        while (! Object.class.equals(type)) {
            if ( ! filter.acceptType(type)) {
                break;
            }
            final Method[] methods = type.getDeclaredMethods();
            for (final Method method : methods) {
                if (filter.acceptMethod(method)) {
                    output.add(method);
                }
            }
            type = type.getSuperclass();
        }
        return output;
    }

    public interface Filter {
        boolean acceptType(Class<?> type);
        boolean acceptMethod(Method method);
    }
}
