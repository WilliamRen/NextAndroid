package com.github.yoojia.next.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventsTest {

    private static class SubscriberHost {

        public final AtomicInteger strCalls = new AtomicInteger(0);
        public final AtomicInteger intCalls = new AtomicInteger(0);
        public final int totalCalls;

        private final CountDownLatch mCountDownLatch;

        private SubscriberHost(int countsPerType) {
            totalCalls = countsPerType * 2;
            mCountDownLatch = new CountDownLatch(totalCalls);
        }

        @Subscribe(async = true)
        public void onEvents(@Evt("str") String start){
            strCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

        @Subscribe(async = true)
        public void onEvents1(@Evt("int") long start){
            intCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

        public void await() throws InterruptedException {
            mCountDownLatch.await();
        }

    }

    private final static int BASE_COUNT = 10000 * 100;

    @Test
    public void testSingleThreadBase(){
        testStress(BASE_COUNT, "SingleThread");
    }


    private void testStress(int count, String tag){
        final NextEvents<Object> events = new NextEvents<>();
        final SubscriberHost subscriberHost = new SubscriberHost(count);
        events.register(subscriberHost, null);

        final long timeBeforeEmits = NOW();
        for (int i = 0; i < count; i++) {

            final long intEvent = NOW();
            events.emit("int", intEvent);

            final String strEvent = String.valueOf(NOW());
            events.emit("str", strEvent);
        }

        final long timeAfterEmits = NOW();

        try {
            subscriberHost.await();
        } catch (InterruptedException e) {
            fail(tag + ", Wait fail");
        }

        events.unregister(subscriberHost);
        events.close();

        assertThat(subscriberHost.intCalls.get(), equalTo(count));
        assertThat(subscriberHost.strCalls.get(), equalTo(count));

        final long timeWhenAllFinished = NOW();
        final long emitMicros = (timeAfterEmits - timeBeforeEmits) / 1000;
        final long deliveredMicros = (timeWhenAllFinished - timeBeforeEmits) / 1000;
        int deliveryRate = (int) (subscriberHost.totalCalls / (deliveredMicros / 1000000d));

        System.err.println(tag + "\tDelivered - " +
                        "RATE:" + deliveryRate + "/s" +
                        "\t\tEMIT:" + TimeUnit.MICROSECONDS.toMillis(emitMicros) + "ms" +
                        "\t\tRUNS:" + TimeUnit.MICROSECONDS.toMillis(deliveredMicros) + "ms" +
                        "\t\tCOUNT:" + subscriberHost.totalCalls
        );
    }

    private static long NOW() {
        return System.nanoTime();
    }

}
