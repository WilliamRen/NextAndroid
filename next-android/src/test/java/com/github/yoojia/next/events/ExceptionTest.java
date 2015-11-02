package com.github.yoojia.next.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Set;
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
        public void on(@Event("test") String evt) {
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
        EventsFlags.enabledPerformanceLog(true);
        EventsFlags.enabledProcessingLog(true);
        NextEvents events = new NextEvents(Schedulers.mainSingle(), "Test");
        Subscriber subscriber = new Subscriber();
        events.register(subscriber, null);
        events.setOnErrorsListener(new OnErrorsListener() {
            @Override
            public void onErrors(EventsException exception) {
                exception.printStackTrace();
            }
        });
        events.emit("HAHA", "test", false);
        subscriber.await();
        events.destroy();
    }
}
