package com.github.yoojia.next.inputs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * NextInputs
 *
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public class NextInputs {

    private static final Comparator<Pattern> ORDERING = new Comparator<Pattern>() {
        @Override
        public int compare(Pattern lhs, Pattern rhs) {
            return lhs.priority - rhs.priority;
        }
    };

    private final ArrayList<TestRule> mRules = new ArrayList<>();

    private MessageDisplay mMessageDisplay = new DefaultMessageDisplay();

    private boolean mAbortOnFail = true;

    public boolean test() throws Exception {
        for (TestRule rule : mRules) {
            boolean passed = true;
            final String input = rule.input.value();
            for (Pattern pattern : rule.patterns) {
                if (!pattern.tester.performTest(input)) {
                    if (mMessageDisplay instanceof DefaultMessageDisplay) {
                        final DefaultMessageDisplay def = (DefaultMessageDisplay) mMessageDisplay;
                        def.attach(rule.input);
                    }
                    mMessageDisplay.show(pattern.message);
                    passed = false;
                    break;
                }
            }
            if (mAbortOnFail && !passed) {
                return false;
            }
        }
        return true;
    }

    public NextInputs add(Input input, Pattern...patterns){
        if (patterns.length == 0){
            throw new IllegalArgumentException("Pattern is required !");
        }
        Arrays.sort(patterns, ORDERING);
        final TestRule rule = new TestRule(input, patterns);
        mRules.add(rule);
        return this;
    }

    public void abortOnFail(boolean abortOnFail){
        mAbortOnFail = abortOnFail;
    }

    public void setMessageDisplay(MessageDisplay display){
        if (display == null) {
            throw new NullPointerException();
        }
        mMessageDisplay = display;
    }

}
