package com.github.yoojia.next.inputs.impls;

import com.github.yoojia.next.inputs.Tester0;

import java.util.regex.Pattern;

/**
 * BlankCard tester
 *
 * @author 陈小锅 (yoojia.chen@gmail.com)
 */
public class BlankCardTester extends Tester0 {

    @Override
    public boolean performTest0(String input) {
        // accept only spaces, digits and dashes
        if (! Pattern.compile("[\\d -]*").matcher(input).matches()) {
            return false;
        }
        String value = input.replaceAll("\\D", "");
        final int length = value.length();
        if ( 13 > length || 19 < length){
            return false;
        }else{
            return matchLuhn(value, length);
        }
    }

    private static boolean matchLuhn(String rawCardNumbers, int length){
        char cDigit;
        int nCheck = 0, nDigit;
        boolean bEven = false;
        for ( int n = length - 1; n >= 0; n--) {
            cDigit = rawCardNumbers.charAt(n);
            nDigit = Integer.parseInt(String.valueOf(cDigit), 10);
            if ( bEven ) {
                if ( (nDigit *= 2) > 9 ) {
                    nDigit -= 9;
                }
            }
            nCheck += nDigit;
            bEven = !bEven;
        }
        return (nCheck % 10) == 0;
    }

}
