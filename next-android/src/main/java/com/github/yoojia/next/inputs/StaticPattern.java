package com.github.yoojia.next.inputs;

import android.text.TextUtils;

import com.github.yoojia.next.inputs.impls.BlankCardTester;
import com.github.yoojia.next.inputs.impls.ChineseIDCardTester;
import com.github.yoojia.next.inputs.impls.NumericTester;

/**
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public class StaticPattern {

    public static final int PRIORITY_REQUIRED = -1024;
    public static final int PRIORITY_GENERAL = 0;

    private static final Tester TESTER_NOT_BLANK = new Tester() {
        @Override
        public boolean performTest(String input) throws Exception {
            if (TextUtils.isEmpty(input)) {
                return false;
            }
            return java.util.regex.Pattern.compile("^\\s*$").matcher(input).matches();
        }
    };

    private static final Tester TESTER_REQUIRED = new Tester() {
        @Override
        public boolean performTest(String input) throws Exception {
            return !TextUtils.isEmpty(input);
        }
    };

    private static final Tester TESTER_DIGITS = new TesterEx() {
        @Override
        public boolean performTest0(String notEmptyInput) throws Exception {
            return TextUtils.isDigitsOnly(notEmptyInput);
        }
    };

    private static final Tester TESTER_EMAIL = new TesterEx() {
        @Override
        public boolean performTest0(String input) throws Exception {
            return matchRegex(input.toLowerCase(), "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+" +
                    "(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+" +
                    "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
        }
    };

    private static final Tester TESTER_HOST = new TesterEx() {
        @Override
        public boolean performTest0(String input) throws Exception {
            return matchRegex(input.toLowerCase(),
                    "^([a-z0-9]([a-z0-9\\-]{0,65}[a-z0-9])?\\.)+[a-z]{2,6}$");
        }
    };

    private static final Tester TESTER_URL = new TesterEx() {
        @Override
        public boolean performTest0(String notEmptyInput) throws Exception {
            return matchRegex(notEmptyInput.toLowerCase(),
                    "^(https?:\\/\\/)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?$");
        }
    };

    private static final Tester TESTER_IPV4 = new TesterEx() {
        @Override
        public boolean performTest0(String notEmptyInput) throws Exception {
            return matchRegex(notEmptyInput,
                    "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        }
    };

    private static final Tester TESTER_CHINESE_MOBILE = new TesterEx() {
        @Override
        public boolean performTest0(String notEmptyInput) throws Exception {
            return matchRegex(notEmptyInput, "^(\\+?\\d{2}-?)?(1[0-9])\\d{9}$");
        }
    };

    private static final Tester TESTER_BLANK_CARD = new BlankCardTester();

    private static final Tester TESTER_CHINESE_ID_CARD = new ChineseIDCardTester();

    private static final Tester TESTER_NUMERIC = new NumericTester();

    /**
     * 必要项，输入内容不能为空
     * @return Pattern
     */
    public static Pattern Required(){
        return new Pattern(TESTER_REQUIRED)
                .priority(PRIORITY_REQUIRED)
                .msgOnFail("此为必填条目");
    }

    /**
     * 输入内容不能为空值：空格，制表符等
     * @return Pattern
     */
    public static Pattern NotBlank(){
        return new Pattern(TESTER_NOT_BLANK)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入非空内容");
    }

    /**
     * 输入内容只能是数字
     * @return Pattern
     */
    public static Pattern Digits(){
        return new Pattern(TESTER_DIGITS)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入数字");
    }

    /**
     * 输入内容为有效的
     * @return Pattern
     */
    public static Pattern Email(){
        return new Pattern(TESTER_EMAIL)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的邮件地址");
    }

    /**
     * 域名地址
     * @return Pattern
     */
    public static Pattern Host(){
        return new Pattern(TESTER_HOST)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的域名地址");
    }

    /**
     * URL地址
     * @return Pattern
     */
    public static Pattern URL(){
        return new Pattern(TESTER_URL)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的网址");
    }

    /**
     * IPV4地址
     * @return Pattern
     */
    public static Pattern IPv4(){
        return new Pattern(TESTER_IPV4)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的IP地址");
    }

    /**
     * 数值
     * @return Pattern
     */
    public static Pattern Numeric(){
        return new Pattern(TESTER_NUMERIC)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的数值");
    }

    /**
     * 银行卡号
     * @return Pattern
     */
    public static Pattern BlankCard(){
        return new Pattern(TESTER_BLANK_CARD)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的银行卡/信用卡号码");
    }

    /**
     * 身份证号
     * @return Pattern
     */
    public static Pattern ChineseIDCard(){
        return new Pattern(TESTER_CHINESE_ID_CARD)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的身份证号");
    }

    /**
     * 手机号
     * @return Pattern
     */
    public static Pattern ChineseMobile(){
        return new Pattern(TESTER_CHINESE_MOBILE)
                .priority(PRIORITY_GENERAL)
                .msgOnFail("请输入有效的手机号");
    }

    public static Pattern AcceptIn(final String chars){
        return RegexMatch("[" + chars + "]")
                .priority(PRIORITY_GENERAL);
    }

    public static Pattern RegexMatch(final String regex) {
        return new Pattern(new Tester() {
            @Override
            public boolean performTest(String input) throws Exception {
                return matchRegex(input, regex);
            }
        }).priority(PRIORITY_GENERAL);
    }

    private static boolean matchRegex(String input, String regex) {
        return java.util.regex.Pattern.compile(regex).matcher(input).matches();
    }
}
