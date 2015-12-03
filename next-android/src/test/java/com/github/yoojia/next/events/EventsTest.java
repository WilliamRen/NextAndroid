package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;

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

    private final static int COUNT_NOP = 10000 * 100;
    private final static int COUNT_PAYLOAD = 1000;

    private static class Payload {
        public final AtomicInteger strCalls = new AtomicInteger(0);
        public final AtomicInteger intCalls = new AtomicInteger(0);
        public final int eventCount;
        public final int totalCalls;

        protected final CountDownLatch mCountDownLatch;

        protected Payload(int count) {
            eventCount = count;
            totalCalls = count * 2;
            mCountDownLatch = new CountDownLatch(totalCalls);
        }

        public final void await() throws InterruptedException {
            mCountDownLatch.await();
        }
    }

    private static class NopPayload extends Payload{

        protected NopPayload(int count) {
            super(count);
        }

        @Subscribe(onThreads = true)
        public void onEvents(@Evt("str") String start){
            strCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

        @Subscribe(onThreads = true)
        public void onEvents1(@Evt("int") long start){
            intCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

    }

    private static class Ms1Payload extends Payload{

        protected Ms1Payload(int count) {
            super(count);
        }

        @Subscribe(onThreads = true)
        public void onEvents(@Evt("str") String start) throws InterruptedException {
            Thread.sleep(1);
            strCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

        @Subscribe(onThreads = true)
        public void onEvents1(@Evt("int") long start) throws InterruptedException {
            Thread.sleep(1);
            intCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

    }

    @Test
    public void testNop1(){
        testStress(new NopPayload(COUNT_NOP), Schedules.singleThread() , "SingleThread(NopPayload)");
    }

    @Test
    public void testNop2(){
        testStress(new NopPayload(COUNT_NOP), Schedules.threads(4), "MultiThreads(NopPayload)");
    }

    @Test
    public void testNop3(){
        testStress(new NopPayload(COUNT_NOP), Schedules.caller(), "CallerThread(NopPayload)");
    }

    @Test
    public void test1ms1(){
        testStress(new Ms1Payload(COUNT_PAYLOAD), Schedules.singleThread() , "SingleThread(1ms Payload)");
    }

    @Test
    public void test1ms2(){
        testStress(new Ms1Payload(COUNT_PAYLOAD), Schedules.threads(4), "MultiThreads(1ms Payload)");
    }

    @Test
    public void test1ms3(){
        testStress(new Ms1Payload(COUNT_PAYLOAD), Schedules.caller(), "CallerThread(1ms Payload)");
    }

    private void testStress(Payload payload, Schedule schedule, String tag){
        final NextEvents events = new NextEvents(schedule);

        events.register(payload, null);

        final long timeBeforeEmits = NOW();
        for (int i = 0; i < payload.eventCount; i++) {

            final long intEvent = NOW();
            events.emit("int", intEvent);

            final String strEvent = String.valueOf(NOW());
            events.emit("str", strEvent);
        }

        final long timeAfterEmits = NOW();

        try {
            payload.await();
        } catch (InterruptedException e) {
            fail(tag + ", Wait fail");
        }

        events.unregister(payload);
        events.close();

        assertThat(payload.intCalls.get(), equalTo(payload.eventCount));
        assertThat(payload.strCalls.get(), equalTo(payload.eventCount));

        final long timeWhenAllFinished = NOW();
        final long emitMicros = (timeAfterEmits - timeBeforeEmits) / 1000;
        final long deliveredMicros = (timeWhenAllFinished - timeBeforeEmits) / 1000;
        int deliveryRate = (int) (payload.totalCalls / (deliveredMicros / 1000000d));

        System.err.println(tag + "\t ###Statistics### " +
                        "Delivered:" + deliveryRate + "/s" +
                        "\t\tEmit:" + TimeUnit.MICROSECONDS.toMillis(emitMicros) + "ms" +
                        "\t\tRuns:" + TimeUnit.MICROSECONDS.toMillis(deliveredMicros) + "ms" +
                        "\t\tCalls:" + payload.totalCalls
        );
    }

    private static long NOW() {
        return System.nanoTime();
    }

}
