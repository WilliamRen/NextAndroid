package com.github.yoojia.next.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ExceptionTest {

    public static class Subscriber {

        private final CountDownLatch mCountDownLatch = new CountDownLatch(1);

        @Subscribe
        public void on(@E("test") String evt) {
            mCountDownLatch.countDown();
            System.err.println("- Test NPE");
            throw new NullPointerException("TEST NPE");
        }

        public void await() throws InterruptedException {
            mCountDownLatch.await();
        }
    }

    @Test
    public void test() throws InterruptedException {
        NextEvents<Object> events = new NextEvents<>();
        Subscriber subscriber = new Subscriber();
        events.register(events, null);
        events.emit("test", "HAHAHA");
        subscriber.await();
        events.close();
    }
}
