package com.github.yoojia.next.events;

import com.github.yoojia.next.react.OnTargetMissListener;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RegTest{

    @Test
    public void test() throws InterruptedException {
        final Target target = new Target();
        final CountDownLatch missCount = new CountDownLatch(3);
        final NextEvents events = new NextEvents();
        events.setOnTargetMissListener(new OnTargetMissListener<Meta>() {
            @Override
            public void onTargetMiss(Meta input) {
                System.err.println("> miss input: " + input);
                missCount.countDown();
            }
        });
        events.register(target, null);
        events.emit("test", "EVT-VALUE-0");
        Assert.assertEquals(1, target.counter.get());

        events.register(target, null);
        events.emit("test", "EVT-VALUE-1");
        Assert.assertEquals(2, target.counter.get());

        events.unregister(target);
        events.emit("test", "WILL-MISS-EVT-VALUE-0");
        Assert.assertEquals(2, target.counter.get());

        events.unregister(target);
        events.emit("test", "WILL-MISS-EVT-VALUE-1");
        events.emit("test", "WILL-MISS-EVT-VALUE-2");
        Assert.assertEquals(2, target.counter.get());

        missCount.await();
    }

    private static class Target {

        final AtomicInteger counter = new AtomicInteger(0);

        @Subscribe(on = "test")
        public void subscriber(String evt) {
            counter.incrementAndGet();
        }
    }

}
