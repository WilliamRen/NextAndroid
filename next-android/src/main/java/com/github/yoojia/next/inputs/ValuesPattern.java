package com.github.yoojia.next.inputs;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public class ValuesPattern {

    public static final int PRIORITY_REQUIRED = StaticPattern.PRIORITY_REQUIRED;
    public static final int PRIORITY_GENERAL = StaticPattern.PRIORITY_GENERAL;

    public static Pattern Required(){
        return StaticPattern.Required();
    }

    public static Pattern MinLength(final int minLength) {
        return new Pattern(new TesterEx() {
            @Override
            public boolean performTest0(String input) throws Exception {
                return input.length() >= minLength;
            }
        }).msgOnFail("输入内容长度必须不少于：" + minLength);
    }

    public static Pattern MaxLength(final int maxLength) {
        return new Pattern(new TesterEx() {
            @Override
            public boolean performTest0(String input) throws Exception {
                return input.length() <= maxLength;
            }
        }).msgOnFail("输入内容长度必须不多于：" + maxLength);
    }

    public static Pattern RangeLength(final int minLength, final int maxLength) {
        return new Pattern(new TesterEx() {
            @Override
            public boolean performTest0(String input) throws Exception {
                final int length = input.length();
                return minLength <= length && length <= maxLength;
            }
        }).msgOnFail("输入内容长度必须在[" + maxLength + "," + maxLength + "]之间");
    }

    public static Pattern MinValue(final int minValue) {
        return proxy(new ValuesProxy<Integer>() {
            @Override
            protected Integer value0() {
                return minValue;
            }

            @Override
            protected Integer valueOf(String input) {
                return Integer.valueOf(input);
            }

            @Override
            protected boolean test(Integer input, Integer value0, Integer value1) {
                return input >= value0;
            }
        }).msgOnFail("输入数值大小必须不小于：" + minValue);
    }

    public static Pattern MinValue(final long minValue) {
        return proxy(new ValuesProxy<Long>() {
            @Override
            protected Long value0() {
                return minValue;
            }

            @Override
            protected Long valueOf(String input) {
                return Long.valueOf(input);
            }

            @Override
            protected boolean test(Long input, Long value0, Long value1) {
                return input >= value0;
            }
        }).msgOnFail("输入数值大小必须不小于：" + minValue);
    }

    public static Pattern MinValue(final float minValue) {
        return proxy(new ValuesProxy<Float>() {
            @Override
            protected Float value0() {
                return minValue;
            }

            @Override
            protected Float valueOf(String input) {
                return Float.valueOf(input);
            }

            @Override
            protected boolean test(Float input, Float value0, Float value1) {
                return input >= value0;
            }
        }).msgOnFail("输入数值大小必须不小于：" + minValue);
    }

    public static Pattern MinValue(final double minValue) {
        return proxy(new ValuesProxy<Double>() {
            @Override
            protected Double value0() {
                return minValue;
            }

            @Override
            protected Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            protected boolean test(Double input, Double value0, Double value1) {
                return input >= value0;
            }
        }).msgOnFail("输入数值大小必须不小于：" + minValue);
    }

    public static Pattern MaxValue(final int maxValue) {
        return proxy(new ValuesProxy<Integer>() {
            @Override
            protected Integer value0() {
                return maxValue;
            }

            @Override
            protected Integer valueOf(String input) {
                return Integer.valueOf(input);
            }

            @Override
            protected boolean test(Integer input, Integer value0, Integer value1) {
                return input <= value0;
            }
        }).msgOnFail("输入数值大小必须不大于：" + maxValue);
    }

    public static Pattern MaxValue(final long maxValue) {
        return proxy(new ValuesProxy<Long>() {
            @Override
            protected Long value0() {
                return maxValue;
            }

            @Override
            protected Long valueOf(String input) {
                return Long.valueOf(input);
            }

            @Override
            protected boolean test(Long input, Long value0, Long value1) {
                return input <= value0;
            }
        }).msgOnFail("输入数值大小必须不大于：" + maxValue);
    }

    public static Pattern MaxValue(final float maxValue) {
        return proxy(new ValuesProxy<Float>() {
            @Override
            protected Float value0() {
                return maxValue;
            }

            @Override
            protected Float valueOf(String input) {
                return Float.valueOf(input);
            }

            @Override
            protected boolean test(Float input, Float value0, Float value1) {
                return input <= value0;
            }
        }).msgOnFail("输入数值大小必须不大于：" + maxValue);
    }

    public static Pattern MaxValue(final double maxValue) {
        return proxy(new ValuesProxy<Double>() {
            @Override
            protected Double value0() {
                return maxValue;
            }

            @Override
            protected Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            protected boolean test(Double input, Double value0, Double value1) {
                return input <= value0;
            }
        }).msgOnFail("输入数值大小必须不大于：" + maxValue);
    }

    public static Pattern RangeValue(final int minValue, final int maxValue) {
        return proxy(new ValuesProxy<Integer>() {
            @Override
            protected Integer value0() {
                return minValue;
            }

            @Override
            protected Integer value1() {
                return maxValue;
            }

            @Override
            protected Integer valueOf(String input) {
                return Integer.valueOf(input);
            }

            @Override
            protected boolean test(Integer input, Integer value0, Integer value1) {
                return value0 <= input && input <= value1;
            }
        }).msgOnFail("输入数值大小必须在[" + minValue + "," + maxValue + "]之间");
    }

    public static Pattern RangeValue(final long minValue, final long maxValue) {
        return proxy(new ValuesProxy<Long>() {
            @Override
            protected Long value0() {
                return minValue;
            }

            @Override
            protected Long value1() {
                return maxValue;
            }

            @Override
            protected Long valueOf(String input) {
                return Long.valueOf(input);
            }

            @Override
            protected boolean test(Long input, Long value0, Long value1) {
                return value0 <= input && input <= value1;
            }
        }).msgOnFail("输入数值大小必须在[" + minValue + "," + maxValue + "]之间");
    }

    public static Pattern RangeValue(final float minValue, final float maxValue) {
        return proxy(new ValuesProxy<Float>() {
            @Override
            protected Float value0() {
                return minValue;
            }

            @Override
            protected Float value1() {
                return maxValue;
            }

            @Override
            protected Float valueOf(String input) {
                return Float.valueOf(input);
            }

            @Override
            protected boolean test(Float input, Float value0, Float value1) {
                return value0 <= input && input <= value1;
            }
        }).msgOnFail("输入数值大小必须在[" + minValue + "," + maxValue + "]之间");
    }

    public static Pattern RangeValue(final double minValue, final double maxValue) {
        return proxy(new ValuesProxy<Double>() {
            @Override
            protected Double value0() {
                return minValue;
            }

            @Override
            protected Double value1() {
                return maxValue;
            }

            @Override
            protected Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            protected boolean test(Double input, Double value0, Double value1) {
                return value0 <= input && input <= value1;
            }
        }).msgOnFail("输入数值大小必须在[" + minValue + "," + maxValue + "]之间");
    }

    public static Pattern EqualsTo(final ValueLoader<String> loader){
        return proxy(new ValuesProxy<String>() {
            @Override
            protected String value0() {
                return loader.value0();
            }

            @Override
            protected String valueOf(String input) {
                return input;
            }

            @Override
            protected boolean test(String input, String value0, String value1) {
                return input.equals(value0);
            }
        });
    }

    public static Pattern NotEqualsTo(final ValueLoader<String> loader){
        return proxy(new ValuesProxy<String>() {
            @Override
            protected String value0() {
                return loader.value0();
            }

            @Override
            protected String valueOf(String input) {
                return input;
            }

            @Override
            protected boolean test(String input, String value0, String value1) {
                return ! input.equals(value0);
            }
        });
    }

    public static <T> Pattern proxy(final ValuesProxy<T> proxy) {
        return new Pattern(new TesterEx() {
            @Override
            public boolean performTest0(String input) throws Exception {
                final T value = proxy.valueOf(input);
                return proxy.test(value, proxy.value0(), proxy. value1());
            }
        }).priority(PRIORITY_GENERAL);
    }

}
