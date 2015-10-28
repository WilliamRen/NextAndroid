package com.github.yoojia.next.events;

import java.util.Map;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
public interface Subscriber {

    void call(Map<String, Object> values) throws Exception;

}
