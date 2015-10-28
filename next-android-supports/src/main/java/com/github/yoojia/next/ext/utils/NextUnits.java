package com.github.yoojia.next.ext.utils;

import java.util.ArrayList;
import java.util.List;

public class NextUnits {

    private static class Section{
        /**
         * 计量单位名
         */
        final String unitName;
        /**
         * 量级
         */
        final long radix;

        /**
         * 前一量级
         */
        final long preRadix;

        Section(String unitChar, long radix, long preRadix){
            this.unitName = unitChar;
            this.radix = radix;
            this.preRadix = preRadix;
        }
    }

    private final List<Section> mSections = new ArrayList<Section>();

    /**
     * 各级单位的连接符
     */
    private String mLinkChar = "";

    /**
     * 单个单位表示时，数值精度位数
     */
    private int mPrecision = 3;

    /**
     * 单个单位时，强制要求显示精度
     */
    private boolean mEnforcePrecision = false;

    /**
     * 第一个单位，其基数为1.
     * @param unit 单位名称
     * @return Tissue对象
     */
    public static NextUnits first(String unit){
        return first(unit, 1);
    }

    /**
     * 第一个单位，并设定基数。
     * @param unit 单位名称
     * @param radix 基数
     * @return Tissue对象
     */
    public static NextUnits first(String unit, int radix){
        NextUnits item = new NextUnits();
        item.mSections.add(new Section(unit, radix, radix));
        return item;
    }

    /**
     * 设定下一个单位及其基数。
     * @param unit 单位名称
     * @param radix 与上一级的基数
     * @return Tissue对象
     */
    public NextUnits next(String unit, int radix){
        Section last = mSections.get(mSections.size() - 1);
        mSections.add(new Section(unit, last.radix * radix, radix));
        return this;
    }

    /**
     * 设定下一个单位，其基数与上一级相同。
     * @param unit 单位名称
     * @return Tissue对象
     */
    public NextUnits next(String unit){
        Section last = mSections.get(mSections.size() - 1);
        mSections.add(new Section(unit, last.radix * last.preRadix, last.preRadix));
        return this;
    }

    /**
     * 设定各级单位之间的连接符
     * @param linkChar 连接符
     * @return Tissue对象
     */
    public NextUnits setLinkChar(String linkChar){
        this.mLinkChar = linkChar;
        return this;
    }

    /**
     * 设定单个单位的精度
     * @param precision 精度小数位数
     * @return Tissue对象
     */
    public NextUnits setPrecision(int precision){
        this.mPrecision = precision;
        return this;
    }

    /**
     * 强制要求数值精度，在单个单位时生效
     * @param enforce
     */
    public NextUnits enforcePrecision(boolean enforce){
        this.mEnforcePrecision = enforce;
        return this;
    }

    /**
     * 格式化数值
     * @param value 数值
     * @return 格式化的数值
     */
    public String format(double value){
        // 0值不需要转换
        if (value == 0){
            final Section sec = mSections.get(0);
            return "0" + sec.unitName;
        }
        final int deep = mSections.size() - 1;
        if (deep == 0){
            return singleUnit(value);
        }else{
            return multiUnit(value, deep);
        }
    }

    private String singleUnit(double value){
        // 处理负数问题
        final StringBuilder msg = new StringBuilder( value < 0 ? "-" : "" );
        value = Math.abs(value);
        final Section sec = mSections.get(0);
        double result = value / sec.radix;
        long intResult = (long)result;
        // 可换成整数，并且没有强制显示精度
        if (result == intResult && !mEnforcePrecision){
            msg.append(intResult);
        }else{
            msg.append(String.format("%." + mPrecision + "f", result));
        }
        msg.append(sec.unitName);
        return msg.toString();
    }

    private String multiUnit(double value, int deep){
        // 处理负数问题
        final StringBuilder msg = new StringBuilder( value < 0 ? "-" : "" );
        value = Math.abs(value);
        for (int i = deep; i >= 0; --i){
            Section sec = mSections.get(i);
            double result = value / sec.radix;
            int intResult = (int)result;
            if (intResult <= 0){
                continue;
            }else{
                msg.append(intResult);
            }
            msg.append(sec.unitName);
            value -= intResult * sec.radix;
            // 如果不是最后一个计量单位，则添加连接符
            if ( i > 0 && value != 0){
                msg.append(mLinkChar);
            }
        }
        return msg.toString();
    }

}