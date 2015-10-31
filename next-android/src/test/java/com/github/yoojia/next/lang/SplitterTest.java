package com.github.yoojia.next.lang;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 * @since 1.0
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SplitterTest {

    @Test
    public void test(){
        List<String> output = Splitter.on(',').split("1,23,A,-+");
        Assert.assertThat(output.size(), equalTo(4));
        Assert.assertThat(output.get(0), equalTo("1"));
        Assert.assertThat(output.get(1), equalTo("23"));
        Assert.assertThat(output.get(2), equalTo("A"));
        Assert.assertThat(output.get(3), equalTo("-+"));
    }
}
