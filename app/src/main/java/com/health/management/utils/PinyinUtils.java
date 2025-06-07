package com.health.management.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音工具类
 */
public class PinyinUtils {

    /**
     * 将中文字符串转换为拼音（不带音调）
     *
     * @param text 需要转换的中文字符串
     * @return 转换后的拼音字符串
     */
    public static String toPinyin(String text) {
        if (text == null || text.length() == 0) {
            return "";
        }

        // 创建拼音输出格式
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 不带音调
        format.setVCharType(HanyuPinyinVCharType.WITH_V); // ü用v表示

        StringBuilder pinyinBuilder = new StringBuilder();
        char[] chars = text.toCharArray();

        try {
            for (char c : chars) {
                // 判断是否为汉字
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                    // 汉字转拼音（取第一个结果）
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyin != null && pinyin.length > 0) {
                        pinyinBuilder.append(pinyin[0]);
                    }
                } else {
                    // 非汉字直接添加
                    pinyinBuilder.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            // 如果转换失败，返回原字符串
            return text;
        }

        return pinyinBuilder.toString();
    }

    /**
     * 检测字符串是否包含中文字符
     *
     * @param text 待检测字符串
     * @return 是否包含中文字符
     */
    public static boolean containsChinese(String text) {
        if (text == null || text.length() == 0) {
            return false;
        }

        for (char c : text.toCharArray()) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                return true;
            }
        }
        return false;
    }
}
