package com.github.yoojia.next.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ErrorsTest {

    @Subscribe
    public void onErrors(@Evt(ExceptionEvent.NAME)ExceptionEvent event){
        System.err.println("----------- Events: exception");
        event.exception.printStackTrace();
    }

    @Subscribe
    public void throwsError(@Evt("err") String act){
        System.err.println("----------- Events: " + act);
        throw new NullPointerException("HAHAHA");
    }

    @Subscribe
    public void calls(@Evt("nonerr") String act){
        System.err.println("----------- Events: " + act);
    }

    @Subscribe
    public void calls(@Evt("001") Integer act){
        System.err.println("----------- Events: " + act);
    }

    @Test
    public void test(){
        NextEvents<Object> events = new NextEvents<>();
        events.register(this, null);
        events.emit("err", "HAHAHA-ERROR");
        events.emit("nonerr", "HAHAHA-OKOK");
        events.emit("001", 123);
        events.unregister(this);
        events.close();
    }
}
