package com.github.yoojia.next.lang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class JoinerTest {

    @Test
    public void test(){
        String output = Joiner.on(',').join(new Integer[]{1,23,45,78});
        assertThat(output, equalTo("1,23,45,78"));
    }
}
