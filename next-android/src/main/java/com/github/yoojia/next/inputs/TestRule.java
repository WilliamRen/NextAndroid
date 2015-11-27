package com.github.yoojia.next.inputs;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
class TestRule {

    public final Input input;
    public final Pattern[] patterns;

    public TestRule(Input input, Pattern[] patterns) {
        this.input = input;
        this.patterns = patterns;
    }
}
