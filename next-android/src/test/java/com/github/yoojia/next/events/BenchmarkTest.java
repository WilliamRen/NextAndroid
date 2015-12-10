package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private static class NextNopThreadsPayload extends Payload{

        protected NextNopThreadsPayload(int count) {
            super(count);
        }

        @Subscribe(runOn = RunOn.THREADS)
        public void onEvents(@Evt("str") String start){
            hitEvt1();
        }

        @Subscribe(runOn = RunOn.THREADS)
        public void onEvents1(@Evt("long") long start){
            hitEvt2();
        }

    }

    private static class NextNopCallerPayload extends Payload{

        protected NextNopCallerPayload(int count) {
            super(count);
        }

        @Subscribe(runOn = RunOn.CALLER)
        public void onEvents(@Evt("str") String start){
            hitEvt1();
        }

        @Subscribe(runOn = RunOn.CALLER)
        public void onEvents1(@Evt("long") long start){
            hitEvt2();
        }

    }

    private static class Next1msThreadsPayload extends Payload{

        protected Next1msThreadsPayload(int count) {
            super(count);
        }

        @Subscribe(runOn = RunOn.THREADS)
        public void onEvents(@Evt("str") String start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt1();
        }

        @Subscribe(runOn = RunOn.THREADS)
        public void onEvents1(@Evt("long") long start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt2();
        }

    }

    private static class Next1msCallerPayload extends Payload{

        protected Next1msCallerPayload(int count) {
            super(count);
        }

        @Subscribe(runOn = RunOn.CALLER)
        public void onEvents(@Evt("str") String start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt1();
        }

        @Subscribe(runOn = RunOn.CALLER)
        public void onEvents1(@Evt("long") long start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt2();
        }

    }

    private static class OttoNopPayload extends Payload{

        protected OttoNopPayload(int count) {
            super(count);
        }

        @com.squareup.otto.Subscribe
        public void onEvents(String evt) {
            hitEvt1();
        }

        @com.squareup.otto.Subscribe
        public void onEvents1(Long evt) {
            hitEvt2();
        }
    }

    private static class Otto1msPayload extends Payload{

        protected Otto1msPayload(int count) {
            super(count);
        }

        @com.squareup.otto.Subscribe
        public void onEvents(String evt) throws InterruptedException {
            Thread.sleep(1);
            hitEvt1();
        }

        @com.squareup.otto.Subscribe
        public void onEvents1(Long evt) throws InterruptedException {
            Thread.sleep(1);
            hitEvt2();
        }
    }

    private final ExecutorService CPUs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    @Test
    public void testNop1(){
        nextStress(new NextNopThreadsPayload(COUNT_NOP), Schedules.newService(CPUs), "MultiThreads(Nop Payload)");
    }

    @Test
    public void testNop2(){
        nextStress(new NextNopThreadsPayload(COUNT_NOP), Schedules.sharedThreads(), "SharedThread(Nop Payload)");
    }

    @Test
    public void testNop3(){
        nextStress(new NextNopCallerPayload(COUNT_NOP), Schedules.newCaller(), "CallerThread(Nop Payload)");
    }

    @Test
    public void testNop4(){
        ottoStress(new OttoNopPayload(COUNT_NOP), "SQUARE.OTTO (Nop Payload)");
    }

    @Test
    public void test1ms1(){
        nextStress(new Next1msThreadsPayload(COUNT_PAYLOAD), Schedules.newService(CPUs), "MultiThreads(1ms Payload)");
    }

    @Test
    public void test1ms2(){
        nextStress(new Next1msThreadsPayload(COUNT_PAYLOAD), Schedules.sharedThreads(), "SharedThread(1ms Payload)");
    }

    @Test
    public void test1ms3(){
        nextStress(new Next1msCallerPayload(COUNT_PAYLOAD), Schedules.newCaller(), "CallerThread(1ms Payload)");
    }

    @Test
    public void test1ms4(){
        ottoStress(new Otto1msPayload(COUNT_PAYLOAD), "SQUARE.OTTO (1ms Payload)");
    }

    private void ottoStress(Payload payload, String tag) {
        final Bus bus = new Bus(ThreadEnforcer.ANY);

        bus.register(payload);

        final long timeBeforeEmits = NOW();

        for (int i = 0; i < payload.perEvtCount; i++) {

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

        assertThat(payload.evt1Calls.get(), equalTo(payload.perEvtCount));
        assertThat(payload.evt2Calls.get(), equalTo(payload.perEvtCount));

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

        for (int i = 0; i < payload.perEvtCount; i++) {

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

        assertThat(payload.evt1Calls.get(), equalTo(payload.perEvtCount));
        assertThat(payload.evt2Calls.get(), equalTo(payload.perEvtCount));

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
