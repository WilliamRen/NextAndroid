package com.github.yoojia.next.inputs;

import com.github.yoojia.next.inputs.impls.ChineseIDCardTester;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test ChineseI DCard Number
 *
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ChineseIDCardTest {

    private ChineseIDCardTester mTester;

    @Before
    public void before(){
        mTester = new ChineseIDCardTester();
    }

    @Test
    public void testPassed() throws Exception {
        Assert.assertTrue(mTester.performTest("440901197709194316"));
        Assert.assertTrue(mTester.performTest("440901197502198379"));
        Assert.assertTrue(mTester.performTest("44090119840325427X"));
        Assert.assertTrue(mTester.performTest("44090119840325427x"));
        Assert.assertTrue(mTester.performTest("210303198412082729"));
    }

    @Test
    public void testFail() throws Exception {
        Assert.assertFalse(mTester.performTest("+440901197709194316"));
        Assert.assertFalse(mTester.performTest("H440901197502198379"));
        Assert.assertFalse(mTester.performTest("A44090119840325427X"));
        Assert.assertFalse(mTester.performTest("0440901199006249711"));
    }
}
