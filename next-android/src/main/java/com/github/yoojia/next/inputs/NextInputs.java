package com.github.yoojia.next.inputs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static com.github.yoojia.next.lang.Preconditions.notNull;

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

    public boolean test(){
        try{
            for (TestRule rule : mRules) {
                if ( ! performTest(rule) && mAbortOnFail) {
                    return false;
                }
            }
            return true;
        }catch (Exception err) {
            throw new RuntimeException(err);
        }
    }

    public NextInputs add(Input input, Pattern...patterns){
        if (patterns == null || patterns.length == 0){
            throw new IllegalArgumentException("Patterns is required !");
        }
        Arrays.sort(patterns, ORDERING);
        mRules.add(new TestRule(input, patterns));
        return this;
    }

    public void abortOnFail(boolean abortOnFail){
        mAbortOnFail = abortOnFail;
    }

    public void setMessageDisplay(MessageDisplay display){
        notNull(display);
        mMessageDisplay = display;
    }

    private boolean performTest(TestRule rule) throws Exception {
        final String value = rule.input.value();
        for (Pattern pattern : rule.patterns) {
            if ( ! pattern.tester.performTest(value)) {
                tryShowMessage(rule.input, pattern.message);
                return false;
            }
        }
        return true;
    }

    private void tryShowMessage(Input input, String message){
        if (mMessageDisplay instanceof DefaultMessageDisplay) {
            final DefaultMessageDisplay def = (DefaultMessageDisplay) mMessageDisplay;
            def.attach(input);
        }
        mMessageDisplay.show(message);
    }
}
