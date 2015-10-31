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

    private static class Subscriber {

        public final AtomicInteger strCalls = new AtomicInteger(0);
        public final AtomicInteger intCalls = new AtomicInteger(0);
        public final int totalCalls;

        private final CountDownLatch mCountDownLatch;

        private Subscriber(int countsPerType) {
            totalCalls = countsPerType * 2;
            mCountDownLatch = new CountDownLatch(totalCalls);
        }

        @Subscribe(async = true)
        public void onEvents(@Event("str") String start){
            strCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

        @Subscribe
        public void onEvents1(@Event("int") long start){
            intCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

        public void await() throws InterruptedException {
            mCountDownLatch.await();
        }

    }

    @Test
    public void testSingleThreadStress(){
        testStress(Schedulers.single(), "SingleThread");
    }

    @Test
    public void testCPUx1ThreadsStress(){
        testStress(Schedulers.threads(), "CPUx1Threads");
    }

    @Test
    public void testCPUx2ThreadsStress(){
        testStress(Schedulers.threads(Runtime.getRuntime().availableProcessors() * 2), "CPUx2Threads");
    }

    @Test
    public void testCPUx4ThreadsStress(){
        testStress(Schedulers.threads(Runtime.getRuntime().availableProcessors() * 4), "CPUx4Threads");
    }

    private void testStress(Schedulers threads, String tag){
        final int count = 10000 * 100;
        final NextEvents events = new NextEvents(threads, tag);
        final Subscriber subscriber = new Subscriber(count);
        events.register(subscriber, null);

        final long timeBeforeEmits = NOW();
        for (int i = 0; i < count; i++) {

            final long intEvent = NOW();
            events.emit(intEvent, "int", false);

            final String strEvent = String.valueOf(NOW());
            events.emit(strEvent, "str", false);
        }

        try {
            subscriber.await();
        } catch (InterruptedException e) {
            fail(tag + ", Wait fail");
        }

        events.unregister(subscriber);
        events.destroy();

        assertThat(subscriber.intCalls.get(), equalTo(count));
        assertThat(subscriber.strCalls.get(), equalTo(count));

        final long timeWhenAllFinish = NOW();
        final long deliveredMicros = (timeWhenAllFinish - timeBeforeEmits) / 1000;
        int deliveryRate = (int) (subscriber.totalCalls / (deliveredMicros / 1000000d));

        System.err.println(tag + " - Delivery Rate: " + deliveryRate + "/s");
    }

    private static long NOW() {
        return System.nanoTime();
    }

}
