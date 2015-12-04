package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class BenchmarkTest {

    private final static int COUNT_PAYLOAD = 1000;
    private final static int COUNT_NOP = COUNT_PAYLOAD * 1000;

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

        public void onStringEvents(String start){
            strCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }

        public void onLongEvents(long start){
            intCalls.addAndGet(1);
            mCountDownLatch.countDown();
        }
    }

    private static class NextNopPayload extends Payload{

        protected NextNopPayload(int count) {
            super(count);
        }

        @Subscribe(onThreads = true)
        public void onEvents(@Evt("str") String start){
            onStringEvents(start);
        }

        @Subscribe(onThreads = true)
        public void onEvents1(@Evt("long") long start){
            onLongEvents(start);
        }

    }

    private static class Next1msPayload extends Payload{

        protected Next1msPayload(int count) {
            super(count);
        }

        @Subscribe(onThreads = true)
        public void onEvents(@Evt("str") String start) throws InterruptedException {
            Thread.sleep(1);
            onStringEvents(start);
        }

        @Subscribe(onThreads = true)
        public void onEvents1(@Evt("long") long start) throws InterruptedException {
            Thread.sleep(1);
            onLongEvents(start);
        }

    }

    private static class OttoNopPayload extends Payload{

        protected OttoNopPayload(int count) {
            super(count);
        }

        @com.squareup.otto.Subscribe
        public void onEvents(String evt) {
            onStringEvents(evt);
        }

        @com.squareup.otto.Subscribe
        public void onEvents1(Long evt) {
            onLongEvents(evt);
        }
    }

    private static class Otto1msPayload extends Payload{

        protected Otto1msPayload(int count) {
            super(count);
        }

        @com.squareup.otto.Subscribe
        public void onEvents(String evt) throws InterruptedException {
            Thread.sleep(1);
            onStringEvents(evt);
        }

        @com.squareup.otto.Subscribe
        public void onEvents1(Long evt) throws InterruptedException {
            Thread.sleep(1);
            onLongEvents(evt);
        }
    }

    private final ExecutorService CPUs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    @Test
    public void testNop1(){
        nextStress(new NextNopPayload(COUNT_NOP), Schedules.newService(CPUs), "MultiThreads(Nop Payload)");
    }

    @Test
    public void testNop2(){
        nextStress(new NextNopPayload(COUNT_NOP), Schedules.useShared(), "SharedThread(Nop Payload)");
    }

    @Test
    public void testNop3(){
        ottoStress(new OttoNopPayload(COUNT_NOP), "SQUARE.OTTO (Nop Payload)");
    }

    @Test
    public void test1ms1(){
        nextStress(new Next1msPayload(COUNT_PAYLOAD), Schedules.newService(CPUs), "MultiThreads(1ms Payload)");
    }

    @Test
    public void test1ms2(){
        nextStress(new Next1msPayload(COUNT_PAYLOAD), Schedules.useShared(), "SharedThread(1ms Payload)");
    }

    @Test
    public void test1ms3(){
        ottoStress(new Otto1msPayload(COUNT_PAYLOAD), "SQUARE.OTTO (1ms Payload)");
    }

    private void ottoStress(Payload payload, String tag) {
        final Bus bus = new Bus(ThreadEnforcer.ANY);

        bus.register(payload);

        final long timeBeforeEmits = NOW();

        for (int i = 0; i < payload.eventCount; i++) {

            final long longEvent = NOW();
            bus.post(longEvent);

            final String strEvent = String.valueOf(NOW());
            bus.post(strEvent);
        }

        final long timeAfterEmits = NOW();

        try {
            payload.await();
        } catch (InterruptedException e) {
            fail(tag + ", Wait fail");
        }

        bus.unregister(payload);

        assertThat(payload.intCalls.get(), equalTo(payload.eventCount));
        assertThat(payload.strCalls.get(), equalTo(payload.eventCount));

        final long timeWhenAllFinished = NOW();
        final long emitMicros = (timeAfterEmits - timeBeforeEmits) / 1000;
        final long deliveredMicros = (timeWhenAllFinished - timeBeforeEmits) / 1000;
        int deliveryRate = (int) (payload.totalCalls / (deliveredMicros / 1000000d));

        System.err.println(tag + "\t ### " +
                        "Delivered:" + deliveryRate + "/s" +
                        "\t\tEmit:" + TimeUnit.MICROSECONDS.toMillis(emitMicros) + "ms" +
                        "\t\tRuns:" + TimeUnit.MICROSECONDS.toMillis(deliveredMicros) + "ms" +
                        "\t\tCalls:" + payload.totalCalls
        );
    }

    private void nextStress(Payload payload, Schedule schedule, String tag){
        final NextEvents events = new NextEvents(schedule);

        events.register(payload, null);

        final long timeBeforeEmits = NOW();

        for (int i = 0; i < payload.eventCount; i++) {

            final long longEvent = NOW();
            events.emit("long", longEvent);

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

        assertThat(payload.intCalls.get(), equalTo(payload.eventCount));
        assertThat(payload.strCalls.get(), equalTo(payload.eventCount));

        final long timeWhenAllFinished = NOW();
        final long emitMicros = (timeAfterEmits - timeBeforeEmits) / 1000;
        final long deliveredMicros = (timeWhenAllFinished - timeBeforeEmits) / 1000;
        int deliveryRate = (int) (payload.totalCalls / (deliveredMicros / 1000000d));

        System.err.println(tag + "\t ### " +
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
