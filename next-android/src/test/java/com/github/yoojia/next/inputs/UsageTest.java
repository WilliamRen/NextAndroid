package com.github.yoojia.next.inputs;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class UsageTest {

    @Test
    public void usage(){
        final NextInputs inputs = new NextInputs();
        inputs.add(new Input() {
            @Override public String onLoadValue() {
                return "123";
            }
        }, StaticPattern.Digits());

        Assert.assertTrue(inputs.test());
    }
}
