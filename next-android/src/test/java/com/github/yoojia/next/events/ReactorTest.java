package com.github.yoojia.next.events;

import com.github.yoojia.next.react.Reactor;
import com.github.yoojia.next.react.Schedule;
import com.github.yoojia.next.react.Schedules;
import com.github.yoojia.next.react.Subscriber;
import com.github.yoojia.next.react.Subscription;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.Callable;
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
public class ReactorTest extends BaseTester {

    private final static int COUNT_PAYLOAD = 1000;
    private final static int COUNT_NOP = COUNT_PAYLOAD * 1000 * 5;

    private static class TestSubscriber extends Payload implements Subscriber<String> {

        protected TestSubscriber(int count) {
            super(count);
        }

        @Override
        public void onCall(String input) throws Exception {
            hitEvt1();
            hitEvt2();
        }

        @Override
        public void onErrors(String input, Exception errors) {

        }
    }

    @Test
    public void test(){

        Reactor<String> reactor = new Reactor<>(Schedules.newCaller());

        TestSubscriber payload = new TestSubscriber(COUNT_NOP);

        reactor.add(Subscription.create0(payload, Schedule.FLAG_ON_THREADS));

        final long timeBeforeEmits = System.nanoTime();

        for (int i = 0; i < payload.perEvtCount; i++) {
            final String strEvent = String.valueOf(NOW());
            reactor.emit(strEvent);
        }

        final long timeAfterEmits = NOW();

        try {
            payload.await();
        } catch (InterruptedException e) {
            fail("Reactor Test, Wait fail");
        }

        reactor.remove(payload);

        assertThat(payload.evt1Calls.get(), equalTo(payload.perEvtCount));
        assertThat(payload.evt2Calls.get(), equalTo(payload.perEvtCount));

        final long timeWhenAllFinished = NOW();
        final long emitMicros = (timeAfterEmits - timeBeforeEmits) / 1000;
        final long deliveredMicros = (timeWhenAllFinished - timeBeforeEmits) / 1000;
        int deliveryRate = (int) (payload.totalCalls / (deliveredMicros / 1000000d));

        // OTTO:    2594713, 2621723
        // REACTOR: 4166909, 4435465, 5688249
        // REACTOR: 2993725
        System.err.println("Reactor\t ### " +
                        "Delivered:" + deliveryRate + "/s" +
                        "\t\tEmit:" + TimeUnit.MICROSECONDS.toMillis(emitMicros) + "ms" +
                        "\t\tRuns:" + TimeUnit.MICROSECONDS.toMillis(deliveredMicros) + "ms" +
                        "\t\tCalls:" + payload.totalCalls
        );
    }
}
