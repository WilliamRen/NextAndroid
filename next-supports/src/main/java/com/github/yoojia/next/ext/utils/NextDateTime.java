package com.github.yoojia.next.ext.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 陈永佳 (chengyongjia@parkingwang.com)
 * @since 1.0
 */
public class NextDateTime {

    public static String pretty(String formatDateTime){
        if (TextUtils.isEmpty(formatDateTime)) {
            return formatDateTime;
        }
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date;
        Calendar dateCalendar = Calendar.getInstance();
        try {
            date = format.parse(formatDateTime);
        } catch (ParseException e) {
            return formatDateTime;
        }
        dateCalendar.setTime(date);
        StringBuffer output = new StringBuffer();
        final Calendar nowCalendar = Calendar.getInstance();
        final int year = dateCalendar.get(Calendar.YEAR);
        final int month = dateCalendar.get(Calendar.MONTH);
        final int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        if (year != nowCalendar.get(Calendar.YEAR)) {
            output.append(year);
            output.append("-");
            final int m = month + 1;
            append(m, output);
            output.append("-");
            append(day, output);
        }

        if (month != nowCalendar.get(Calendar.MONTH) && day != nowCalendar.get(Calendar.DAY_OF_MONTH)) {
            final int m = month + 1;
            append(m, output);
            output.append("-");
            append(day, output);
        }

        final int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        final int minute = dateCalendar.get(Calendar.MINUTE);
        final int second = dateCalendar.get(Calendar.SECOND);
        append(hour, output);
        output.append(":");
        append(minute, output);
        output.append(":");
        append(second, output);

        return output.toString();
    }

    private static void append(int value, StringBuffer output){
        if (value <= 9){
            output.append("0").append(value);
        }else{
            output.append(value);
        }
    }
}
