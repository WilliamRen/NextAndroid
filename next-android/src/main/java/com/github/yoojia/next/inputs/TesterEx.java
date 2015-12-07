package com.github.yoojia.next.inputs;

/**
 * Allow empty input
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public abstract class TesterEx extends Tester{

    @Override
    public final boolean performTest(String input) throws Exception {
        if (input == null || input.isEmpty()) {
            return true;
        }
        return performTest0(input);
    }

    public abstract boolean performTest0(String notEmptyInput) throws Exception;
}
