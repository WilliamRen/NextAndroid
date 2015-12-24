package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;

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
public class StressTest extends BaseTester{

    private final static int COUNT_NOP = 10000 * 100;
    private final static int COUNT_PAYLOAD = 1000;

    private static class ThreadsNopPayload extends Payload{

        protected ThreadsNopPayload(int count) {
            super(count);
        }

        @Subscribe(on = "str", run = RunTypes.THREADS)
        public void onEvents(String start){
            hitEvt1();
        }

        @Subscribe(on = "long", run = RunTypes.THREADS)
        public void onEvents1(long start){
            hitEvt2();
        }

    }

    private static class CallerNopPayload extends Payload{

        protected CallerNopPayload(int count) {
            super(count);
        }

        @Subscribe(on = "str", run = RunTypes.CALLER)
        public void onEvents(String start){
            hitEvt1();
        }

        @Subscribe(on = "long", run = RunTypes.CALLER)
        public void onEvents1(long start){
            hitEvt2();
        }

    }

    private static class Threads1msPayload extends Payload{

        protected Threads1msPayload(int count) {
            super(count);
        }

        @Subscribe(on = "str", run = RunTypes.THREADS)
        public void onEvents(String start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt1();
        }

        @Subscribe(on = "long", run = RunTypes.THREADS)
        public void onEvents1(long start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt2();
        }

    }

    private static class Caller1msPayload extends Payload{

        protected Caller1msPayload(int count) {
            super(count);
        }

        @Subscribe(on = "str", run = RunTypes.CALLER)
        public void onEvents(String start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt1();
        }

        @Subscribe(on = "long", run = RunTypes.CALLER)
        public void onEvents1(long start) throws InterruptedException {
            Thread.sleep(1);
            hitEvt2();
        }

    }

    private final ExecutorService CPUs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    @Test
    public void testNop1(){
        testStress(new ThreadsNopPayload(COUNT_NOP), Schedules.newService(CPUs), "MultiThreads(Nop Payload)");
    }

    @Test
    public void testNop2(){
        testStress(new ThreadsNopPayload(COUNT_NOP), Schedules.sharedThreads(), "SharedThreads(Nop Payload)");
    }

    @Test
    public void testNop3(){
        testStress(new CallerNopPayload(COUNT_NOP), Schedules.newCaller(), "CallerThread(Nop Payload)");
    }

    @Test
    public void test1ms1(){
        testStress(new Threads1msPayload(COUNT_PAYLOAD), Schedules.newService(CPUs), "MultiThreads(1ms Payload)");
    }

    @Test
    public void test1ms2(){
        testStress(new Threads1msPayload(COUNT_PAYLOAD), Schedules.sharedThreads(), "SharedThreads(1ms Payload)");
    }

    @Test
    public void test1ms3(){
        testStress(new Caller1msPayload(COUNT_PAYLOAD), Schedules.newCaller(), "CallerThread(1ms Payload)");
    }

    private void testStress(Payload payload, Schedule schedule, String tag){
        final NextEvents events = new NextEvents(schedule);

        events.register(payload, null);

        final long timeBeforeEmits = NOW();
        for (int i = 0; i < payload.perEvtCount; i++) {

            final long intEvent = NOW();
            events.emit("long", intEvent);

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

}
