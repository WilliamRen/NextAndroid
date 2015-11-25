package com.github.yoojia.next.inputs.impls;

import android.text.TextUtils;

import com.github.yoojia.next.inputs.Tester;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


/**
 * FIXME Class description goes here
 *
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 */
public class ChineseIDCardTester extends Tester{

    @Override
    public boolean performTest(String content) {
        if (TextUtils.isEmpty(content)){
            return true;
        }
        final int length = content.length();
        if (15 == length){
            try{
                return isOldCNIDCard(content);
            }catch (NumberFormatException e){
                e.printStackTrace();
                return false;
            }
        }else if (18 == length){
            return isNewCNIDCard(content);
        }else {
            return false;
        }
    }

    static final int[] WEIGHT = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};

    static final char[] VALID = {'1','0','X','9','8','7','6','5','4','3','2'};

    public static boolean isNewCNIDCard(String numbers){
        int sum = 0;
        for (int i = 0; i < WEIGHT.length; i++){
            final int cell = Character.getNumericValue(numbers.charAt(i));
            sum += WEIGHT[i] * cell;
        }
        int index = sum % 11;
        return VALID[index] == numbers.charAt(17);
    }

    public static boolean isOldCNIDCard(String numbers){
        //ABCDEFYYMMDDXXX
        String abcdef = numbers.substring(0,5);
        String yymmdd = numbers.substring(6,11);
        String xxx = numbers.substring(12,14);
        boolean aPass = abcdef.equals(String.valueOf(Integer.parseInt(abcdef)));
        boolean yPass = true;
        try {
            new SimpleDateFormat("YYMMdd").parse(yymmdd);
        } catch (ParseException e) {
            e.printStackTrace();
            yPass = false;
        }
        boolean xPass = Pattern.compile("\\d{2}[\\dxX]").matcher(xxx).matches();
        return aPass && yPass && xPass;
    }
}
