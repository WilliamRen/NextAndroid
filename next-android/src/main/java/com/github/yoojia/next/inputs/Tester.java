package com.github.yoojia.next.inputs;

import android.text.TextUtils;

/**
 * Allow empty input
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public abstract class Tester implements AbstractTester {

    @Override
    public final boolean performTest(String rawInput) throws Exception {
        if (TextUtils.isEmpty(rawInput)) {
            return true;
        }
        return performTestNotEmpty(rawInput);
    }

    public abstract boolean performTestNotEmpty(String notEmptyInput) throws Exception;
}
