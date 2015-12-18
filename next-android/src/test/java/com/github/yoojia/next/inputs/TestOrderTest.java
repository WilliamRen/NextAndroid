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
public class TestOrderTest {

    @Test
    public void testOrder(){
        final NextInputs inputs = new NextInputs();
        final StringBuilder ordered = new StringBuilder();
        inputs.add(new Input() {
            @Override
            public String onLoadValue() {
                return "hahaha";
            }
        }, new Pattern(new AbstractTester() {
            @Override
            public boolean performTest(String rawInput) throws Exception {
                ordered.append("0");
                return true;
            }
        }), new Pattern(new AbstractTester() {
            @Override
            public boolean performTest(String rawInput) throws Exception {
                ordered.append("1");
                return true;
            }
        }), new Pattern(new AbstractTester() {
            @Override
            public boolean performTest(String rawInput) throws Exception {
                ordered.append("2");
                return true;
            }
        }), new Pattern(new AbstractTester() {
            @Override
            public boolean performTest(String rawInput) throws Exception {
                ordered.append("3");
                return true;
            }
        }).priority(StaticPattern.PRIORITY_REQUIRED));

        Assert.assertTrue(inputs.test());
        Assert.assertEquals("3012", ordered.toString());
    }
}
